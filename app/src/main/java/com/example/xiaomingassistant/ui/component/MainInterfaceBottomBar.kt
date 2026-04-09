package com.example.xiaomingassistant.ui.component

import android.content.Context
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

    private fun updateState(selectedIndex: Int) {
        tabList.forEachIndexed { index, layout ->
            // 变色逻辑
            layout.isSelected = (index == selectedIndex)
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.main_interface_footer, this, true)

        // 五个底栏控件
        val tabSkill = findViewById<LinearLayout>(R.id.footer_skill_study)
        val tabNotes = findViewById<LinearLayout>(R.id.footer_notes)
        val tabAi = findViewById<LinearLayout>(R.id.footer_ai)
        val tabLife = findViewById<LinearLayout>(R.id.footer_life)
        val tabSettings = findViewById<LinearLayout>(R.id.footer_settings)

        tabList = listOf(tabSkill, tabNotes, tabAi, tabLife, tabSettings)

        // 按键绑定
        tabList.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                updateState(index)
                onTabSelectedListener?.invoke(index)
            }
        }

        // 默认选中第一个
        updateState(0)
    }
}