package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R

class SettingsFragment : Fragment(R.layout.main_interface_settings) {

    private var currentTopBarHeight = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBarContainer = view.findViewById<View>(R.id.top_bar_container)
        val blurView = view.findViewById<View>(R.id.top_bar_blur_bg)
        val topBarText = view.findViewById<TextView>(R.id.skillstudy_topbar)
        val largeTitle = view.findViewById<TextView>(R.id.tv_main_large_title)
        val scrollView = view.findViewById<NestedScrollView>(R.id.skillstudy_scrollview)
        val contentContainer = view.findViewById<LinearLayout>(R.id.skillstudy_content_container)

        // 1. 沉浸式与高度计算 (避开摄像头)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            currentTopBarHeight = statusBarHeight + dpToPx(60f)

            topBarContainer.updateLayoutParams { height = currentTopBarHeight }
            topBarText.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = statusBarHeight
            }

            // 内容内边距：初始留白
            contentContainer.updatePadding(
                top = currentTopBarHeight + dpToPx(20f),
                bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom + dpToPx(40f)
            )
            insets
        }

        // 2. 核心逻辑：复合 Alpha 滑动联动
        scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            // 计算临界值：大标题相对于顶栏底部的距离
            val startFade = largeTitle.top - currentTopBarHeight
            val endFade = largeTitle.bottom - currentTopBarHeight

            // 计算基础比例 (0 到 1)
            val ratio = when {
                scrollY <= startFade -> 0f
                scrollY >= endFade -> 1f
                else -> (scrollY - startFade).toFloat() / (endFade - startFade)
            }

            // --- 修改点：复合 Alpha 计算 ---
            // 顶栏和模糊背景的 Alpha 逻辑不变
            blurView.alpha = ratio
            topBarText.alpha = ratio

            // 大标题的 Alpha 逻辑修改：
            // 让它在模糊阶段（ratio从0到1的过程中）先保持 alpha=1，
            // 只有当彻底划过去之后，才开始迅速变透明。
            // 我们可以利用 ratio 的平滑性质：只有在 ratio = 1 时，大标题才开始消失。
            val largeTitleAlpha = if (ratio >= 0.9f) {
                // 如果滑动快要到底部（比如比例大于0.9），迅速把 alpha 变小到0
                (1f - ratio) * 10f // 这是为了加快消失速度，ratio在0.9时Alpha仍为1，ratio=1时Alpha=0
            } else {
                1f // 在受模糊影响的主要阶段，保持实体
            }
            // largeTitle.alpha = largeTitleAlpha // 尝试直接赋值可能会因为计算速度问题不自然

            // 另一种更自然的平滑算法：让 Alpha 的变化范围比 ratio 稍微滞后，但更自然
            largeTitle.alpha = if (scrollY < startFade) {
                1f
            } else if (scrollY > endFade + dpToPx(30f)) { // 额外增加 30dp 作为彻底消失的缓冲区
                0f
            } else {
                // 线性插值，范围是 startFade 到 endFade +缓冲区
                val fadeRange = endFade + dpToPx(30f) - startFade
                val progress = (scrollY - startFade).toFloat() / fadeRange
                1f - progress // 这是一个平滑的渐隐
            }
            // --------------
        }

        // 3. 吸附效果 (Snap)
        scrollView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.postDelayed({
                    val scrollY = scrollView.scrollY
                    val startFade = largeTitle.top - currentTopBarHeight
                    val endFade = largeTitle.bottom - currentTopBarHeight
                    val threshold = (startFade + endFade) / 2

                    if (scrollY in startFade..endFade) {
                        if (scrollY < threshold) {
                            scrollView.smoothScrollTo(0, startFade)
                        } else {
                            scrollView.smoothScrollTo(0, endFade)
                        }
                    }
                }, 100)
            }
            false
        }
    }

    private fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}