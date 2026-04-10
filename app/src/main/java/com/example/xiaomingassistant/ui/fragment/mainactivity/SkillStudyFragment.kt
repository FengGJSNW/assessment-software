package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
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
import com.example.xiaomingassistant.ui.view.RealtimeBlurView.RealtimeBlurView
import com.google.android.material.card.MaterialCardView

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    private var lastScrollY = -1
    private var isSnapping = false
    private var currentTopBarHeight = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 基础视图初始化
        val topBarContainer = view.findViewById<View>(R.id.top_bar_container)
        val blurView = view.findViewById<RealtimeBlurView>(R.id.top_bar_blur_bg)
        val topBarText = view.findViewById<TextView>(R.id.skillstudy_topbar)
        val largeTitle = view.findViewById<TextView>(R.id.tv_main_large_title)
        val scrollView = view.findViewById<NestedScrollView>(R.id.skillstudy_scrollview)
        val contentContainer = view.findViewById<LinearLayout>(R.id.skillstudy_content_container)

        // 2. 【核心修复】初始化所有卡片 ID
        val cardLeft = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_left)
        val cardRightTop = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_1)
        val cardRightBottom = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_2)

        // 3. 沉浸式与内外边距适配
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // 更新顶部高度
            currentTopBarHeight = statusBarHeight + dpToPx(60f)
            topBarContainer.updateLayoutParams { height = currentTopBarHeight }
            topBarText.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = statusBarHeight
            }

            // 底部避让：系统导航栏 + 60dp底栏 + 20dp间距
            scrollView.clipToPadding = false
            contentContainer.updatePadding(
                top = currentTopBarHeight + dpToPx(20f),
                bottom = navBarHeight + dpToPx(80f)
            )
            insets
        }

        // 4. 【核心修复】卡片比例动态适配逻辑
        cardLeft.post {
            val width = cardLeft.width
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
            cardLeft.updateLayoutParams { height = width }
            val smallHeight = (width - margin) / 2
            cardRightTop.updateLayoutParams { height = smallHeight }
            cardRightBottom.updateLayoutParams { height = smallHeight }
        }

        // 5. 交互与滑动逻辑
        topBarText.alpha = 0f
        scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            val startFade = largeTitle.top - currentTopBarHeight
            val endFade = largeTitle.bottom - currentTopBarHeight
            handleTitleFade(scrollY, startFade, endFade, topBarText, largeTitle)
            handleRealtimeBlur(scrollY, startFade, endFade, blurView)
        }

        scrollView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                handleSnap(scrollView, largeTitle)
                v.performClick()
            }
            false
        }
    }

    // --- 私有辅助逻辑 ---

    private fun handleTitleFade(scrollY: Int, start: Int, end: Int, barText: TextView, largeTitle: TextView) {
        if (end <= start) return
        val progress = ((scrollY - start).toFloat() / (end - start)).coerceIn(0f, 1f)
        barText.alpha = progress
        largeTitle.alpha = 1f - progress
    }

    private fun handleRealtimeBlur(scrollY: Int, start: Int, end: Int, blurView: RealtimeBlurView) {
        if (scrollY > start && end > start) {
            val progress = ((scrollY - start).toFloat() / (end - start)).coerceIn(0f, 1f)
            blurView.setBlurRadius(dpToPx(progress * 20f).toFloat())
            val alpha = (progress * 153).toInt()
            blurView.setOverlayColor(Color.argb(alpha, 255, 255, 255))
        } else {
            blurView.setBlurRadius(0f)
            blurView.setOverlayColor(Color.TRANSPARENT)
        }
    }

    private fun handleSnap(scrollView: NestedScrollView, title: View) {
        scrollView.postDelayed(object : Runnable {
            override fun run() {
                val currentY = scrollView.scrollY
                if (lastScrollY == currentY) {
                    executeSnapAction(scrollView, title)
                } else {
                    lastScrollY = currentY
                    scrollView.postDelayed(this, 50)
                }
            }
        }, 50)
    }

    private fun executeSnapAction(scrollView: NestedScrollView, title: View) {
        if (isSnapping) return
        val scrollY = scrollView.scrollY
        val titleRelTop = title.top - scrollY
        if (titleRelTop < currentTopBarHeight && (title.bottom - scrollY) > currentTopBarHeight) {
            isSnapping = true
            val ratio = (currentTopBarHeight - titleRelTop).toFloat() / title.height
            if (ratio >= 0.5f) {
                scrollView.smoothScrollTo(0, title.bottom - currentTopBarHeight)
            } else {
                scrollView.smoothScrollTo(0, (title.top - currentTopBarHeight - dpToPx(20f)).coerceAtLeast(0))
            }
            scrollView.postDelayed({ isSnapping = false }, 500)
        }
    }

    private fun dpToPx(dp: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    ).toInt()
}