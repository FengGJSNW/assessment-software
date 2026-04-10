package com.example.xiaomingassistant.ui.component

import android.content.Context
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.xiaomingassistant.R

class MainInterfaceBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var onTabSelectedListener: ((Int) -> Unit)? = null
    private lateinit var tabList: List<LinearLayout>

    // 记录当前选中的索引，防止重复点击触发逻辑
    private var currentSelectedIndex = -1

    init {
        LayoutInflater.from(context).inflate(R.layout.main_interface_footer, this, true)
        setBackgroundColor(Color.TRANSPARENT)

        // 在 MainInterfaceBottomBar 的 init 或 onAttachedToWindow 中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 找到 XML 里的那个背景 View: bottom_blur_bg
            val blurBg = findViewById<View>(R.id.bottom_blur_bg)
            blurBg.setRenderEffect(
                RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP)
            )
        }

        tabList = listOf(
            findViewById(R.id.footer_skill_study),
            findViewById(R.id.footer_notes),
            findViewById(R.id.footer_ai),
            findViewById(R.id.footer_life),
            findViewById(R.id.footer_settings)
        )


        /**
         * 点击回调逻辑
         */
        tabList.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                if (currentSelectedIndex != index) {
                    onTabSelectedListener?.invoke(index)
                }
            }
        }

        setCurrentSelectedIndex(0) // 默认选中第一个

    }// init 结束

    /**
     * 滑动回调逻辑
     */

    // 图标逻辑
    fun setCurrentSelectedIndex(index: Int) {
        if (index < 0 || index >= tabList.size) return
        currentSelectedIndex = index
        updateState(index)
    }

    private fun updateState(selectedIndex: Int) {
        tabList.forEachIndexed { index, layout ->
            // 图标变色
            layout.isSelected = (index == selectedIndex)
        }
    }
}