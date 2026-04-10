package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.google.android.material.card.MaterialCardView
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    private var lastScrollY = -1
    private var isSnapping = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 初始化所有视图变量
        val topBarContainer = view.findViewById<View>(R.id.top_bar_container)
        val topBarText = view.findViewById<TextView>(R.id.skillstudy_topbar)
        val largeTitle = view.findViewById<TextView>(R.id.tv_main_large_title)
        val scrollView = view.findViewById<NestedScrollView>(R.id.skillstudy_scrollview)
        val contentContainer = view.findViewById<LinearLayout>(R.id.skillstudy_content_container)

        val cardLeft = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_left)
        val cardRightTop = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_1)
        val cardRightBottom = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_2)

        // 记录顶栏高度，供吸附逻辑使用
        var currentTopBarHeight = 0

        // 2. 处理状态栏与内边距
        ViewCompat.setOnApplyWindowInsetsListener(topBarContainer) { v: View, insets: WindowInsetsCompat ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(0, statusBar.top, 0, 0)

            val sixtyDpPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                60f,
                resources.displayMetrics
            ).toInt()

            currentTopBarHeight = statusBar.top + sixtyDpPx
            v.updateLayoutParams { height = currentTopBarHeight }

            // 初始 PaddingTop，避开顶栏
            contentContainer.updatePadding(top = currentTopBarHeight + 20)
            insets
        }

        // 3. 标题淡入淡出逻辑
        topBarText.alpha = 0f
        scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            val startFade = largeTitle.top - currentTopBarHeight
            val endFade = largeTitle.bottom - currentTopBarHeight

            if (endFade > startFade) {
                val alpha = ((scrollY - startFade).toFloat() / (endFade - startFade)).coerceIn(0f, 1f)
                topBarText.alpha = alpha
                largeTitle.alpha = 1f - alpha
            }
        }

        // 4. 吸附逻辑：监听触摸结束
        scrollView.setOnTouchListener { _: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_UP) {
                startSnapCheck(scrollView, largeTitle, currentTopBarHeight)
            }
            false
        }

        // 5. 卡片正方形适配
        cardLeft.post {
            val width = cardLeft.width
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
            cardLeft.updateLayoutParams { height = width }
            val smallHeight = (width - margin) / 2
            cardRightTop.updateLayoutParams { height = smallHeight }
            cardRightBottom.updateLayoutParams { height = smallHeight }
        }



        scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            val startFade = largeTitle.top - currentTopBarHeight
            val endFade = largeTitle.bottom - currentTopBarHeight

            if (endFade > startFade) {
                val progress = ((scrollY - startFade).toFloat() / (endFade - startFade)).coerceIn(0f, 1f)
                topBarText.alpha = progress
                largeTitle.alpha = 1f - progress

                // --- 核心修复：添加高斯模糊 ---
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (progress > 0f) {
                        // 根据滚动进度计算模糊半径，最大 20f 效果较好
                        val blurRadius = progress * 20f
                        topBarContainer.setRenderEffect(
                            RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                        )
                    } else {
                        topBarContainer.setRenderEffect(null)
                    }
                }
            } else {
                topBarText.alpha = 0f
                largeTitle.alpha = 1f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    topBarContainer.setRenderEffect(null)
                }
            }
        }
    }

    private fun startSnapCheck(scrollView: NestedScrollView, title: View, topBarHeight: Int) {
        scrollView.postDelayed(object : Runnable {
            override fun run() {
                val currentY = scrollView.scrollY
                if (lastScrollY == currentY) {
                    performSnap(scrollView, title, topBarHeight)
                } else {
                    lastScrollY = currentY
                    scrollView.postDelayed(this, 50)
                }
            }
        }, 50)
    }

    private fun performSnap(scrollView: NestedScrollView, title: View, topBarHeight: Int) {
        if (isSnapping) return

        val scrollY = scrollView.scrollY
        val titleTop = title.top
        val titleBottom = title.bottom

        val titleRelativeTop = titleTop - scrollY
        val titleRelativeBottom = titleBottom - scrollY

        // 只有当大标题正在穿过顶栏边缘时才触发
        if (titleRelativeTop < topBarHeight && titleRelativeBottom > topBarHeight) {
            val titleHeight = title.height
            val coveredHeight = topBarHeight - titleRelativeTop
            val ratio = coveredHeight.toFloat() / titleHeight

            isSnapping = true
            if (ratio >= 0.5f) {
                scrollView.smoothScrollTo(0, titleBottom - topBarHeight)
            } else {
                scrollView.smoothScrollTo(0, (titleTop - topBarHeight - 20).coerceAtLeast(0))
            }
            scrollView.postDelayed({ isSnapping = false }, 500)
        }
    }
}