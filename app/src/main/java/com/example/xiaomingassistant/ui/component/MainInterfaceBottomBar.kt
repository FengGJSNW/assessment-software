package com.example.xiaomingassistant.ui.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.xiaomingassistant.R

class MainInterfaceBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var onTabSelectedListener: ((Int) -> Unit)? = null
    private lateinit var tabList: List<LinearLayout>
    private var currentSelectedIndex = -1

    init {
        // 1. 加载布局
        LayoutInflater.from(context).inflate(R.layout.main_interface_footer, this, true)

        // 2. 确保自定义 View 本身是透明的
        setBackgroundColor(Color.TRANSPARENT)
        orientation = VERTICAL

        // 3. 【修复：已删除】
        // 不再在这里 findViewById(R.id.bottom_bar_bg)，因为该 View 已挪到外部 main_interface.xml

        // 4. 初始化 Tab 列表
        tabList = listOf(
            findViewById(R.id.footer_skill_study),
            findViewById(R.id.footer_notes),
            findViewById(R.id.footer_ai),
            findViewById(R.id.footer_life),
            findViewById(R.id.footer_settings)
        )

        tabList.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                if (currentSelectedIndex != index) {
                    onTabSelectedListener?.invoke(index)
                }
            }
        }

        setCurrentSelectedIndex(0)
    }

    // 更新选中状态的逻辑保持不变
    fun setCurrentSelectedIndex(index: Int) {
        if (index < 0 || index >= tabList.size) return
        currentSelectedIndex = index
        updateState(index)
    }

    private fun updateState(selectedIndex: Int) {
        tabList.forEachIndexed { index, layout ->
            layout.isSelected = (index == selectedIndex)
        }
    }
}