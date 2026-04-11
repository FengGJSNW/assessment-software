package com.example.xiaomingassistant.ui.utils

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.appcompat.widget.Toolbar

object TopBarManager {

    fun setup(
        fragment: Fragment,
        rootView: View,
        title: String,
        @DrawableRes leftButtons: List<Int> = emptyList(),
        @DrawableRes rightButtons: List<Int> = emptyList(),
        onAction: (resId: Int) -> Unit
    ) {
        val appBar = rootView.findViewById<AppBarLayout>(R.id.app_bar_layout)
        val collapsingToolbar = rootView.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        val toolbar = rootView.findViewById<Toolbar>(R.id.main_toolbar)
        val leftBox = rootView.findViewById<LinearLayout>(R.id.left_button_container)
        val rightBox = rootView.findViewById<LinearLayout>(R.id.right_button_container)
        val blurView = rootView.findViewById<View>(R.id.top_bar_blur_bg)

        // 1. 核心修复：强制标题在最上层绘制
        collapsingToolbar.isTitleEnabled = true
        collapsingToolbar.title = title
        // 必须清空 Toolbar 的 Title，否则它会占据中间位置拦截动画
        toolbar.title = ""

        val context = fragment.requireContext()

        // 2. 动态生成图标逻辑
        val dp44 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f, context.resources.displayMetrics).toInt()
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()

        fun addIcons(container: LinearLayout, @DrawableRes ids: List<Int>) {
            container.removeAllViews()
            ids.forEach { id ->
                val iv = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(dp44, dp44)
                    setPadding(padding, padding, padding, padding)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setImageResource(id)
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
                    setBackgroundResource(outValue.resourceId)
                    setOnClickListener { onAction(id) }
                }
                container.addView(iv)
            }
        }

        addIcons(leftBox, leftButtons)
        addIcons(rightBox, rightButtons)

        // 3. 联动逻辑：处理高斯模糊透明度
        blurView.alpha = 0f
        appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (totalScrollRange != 0) {
                val fraction = Math.abs(verticalOffset).toFloat() / totalScrollRange
                // 当折叠超过一定比例，显示高斯模糊
                blurView.alpha = if (fraction > 0.05f) fraction else 0f
            }
        }
    }
}