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


    var onTabSelectedListener: ((Int) -> Unit)? = null // 页面切换接口
    private lateinit var tabList: List<LinearLayout>   // 按钮List
    private var currentSelectedIndex = 0               // 当前下标

    init {
        LayoutInflater.from(context).inflate(R.layout.main_interface_footer, this, true)

        // 保证view透明
        setBackgroundColor(Color.TRANSPARENT)
        orientation = VERTICAL

        // 初始化 Tab 列表
        tabList = listOf(
            findViewById(R.id.footer_skill_study),
            findViewById(R.id.footer_notes),
            findViewById(R.id.footer_ai),
            findViewById(R.id.footer_life),
            findViewById(R.id.footer_settings)
        )

        tabList.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                // 向外传下标
                if (currentSelectedIndex != index) {
                    onTabSelectedListener?.invoke(index)
                }
            }
        }

        setCurrentSelectedIndex(0)
    }

    // 设置当前选中的项
    fun setCurrentSelectedIndex(index: Int) {
        if (index < 0 || index >= tabList.size) return
        currentSelectedIndex = index
        updateState(index)
    }

    // 刷新tab的选中状态
    private fun updateState(selectedIndex: Int) {
        tabList[currentSelectedIndex].isSelected = false
        tabList[selectedIndex].isSelected = true
        currentSelectedIndex = selectedIndex
    }
}