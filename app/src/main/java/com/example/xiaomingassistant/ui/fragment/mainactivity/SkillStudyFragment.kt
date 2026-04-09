package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.google.android.material.card.MaterialCardView

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    // ... 之前的 import 保持不变

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBarContainer = view.findViewById<View>(R.id.top_bar_container)
        val topBarText = view.findViewById<TextView>(R.id.skillstudy_topbar)
        val scrollView = view.findViewById<NestedScrollView>(R.id.skillstudy_scrollview)
        val cardLeft = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_left)
        val cardRightTop = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_1)
        val cardRightBottom = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_2)
        val largeTitle = view.findViewById<TextView>(R.id.tv_main_large_title)

        // 1. 处理摄像头/状态栏穿透
        // 1. 处理状态栏穿透
        ViewCompat.setOnApplyWindowInsetsListener(topBarContainer) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())

            // 给顶栏加内边距
            v.setPadding(0, statusBar.top, 0, 0)

            // 动态计算 60dp 对应的像素
            val sixtyDpPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                60f,
                resources.displayMetrics
            ).toInt()

            v.updateLayoutParams {
                height = statusBar.top + sixtyDpPx
            }

            insets
        }

        // 2. 正方形适配公式：左侧 H = W，右侧两个之和 = 左侧
        cardLeft.post {
            val width = cardLeft.width
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt() // 6dp+6dp的间距

            // 设置左侧大卡片为正方形
            cardLeft.updateLayoutParams { height = width }

            // 设置右侧小卡片：(总高度 - 中间 12dp 间距) / 2
            val smallHeight = (width - margin) / 2
            cardRightTop.updateLayoutParams { height = smallHeight }
            cardRightBottom.updateLayoutParams { height = smallHeight }
        }

        // 3. 标题淡入淡出逻辑
        topBarText.alpha = 0f
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // 当大标题滑出屏幕时（scrollY 达到大标题的底部位置），顶栏文字完全显示
            val threshold = largeTitle.bottom.toFloat()
            val alpha = (scrollY.toFloat() / threshold).coerceIn(0f, 1f)
            topBarText.alpha = alpha
        }
    }
}