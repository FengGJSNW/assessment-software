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
        orientation = VERTICAL
        setBackgroundColor(Color.TRANSPARENT)

        val blurBg = findViewById<View>(R.id.bottom_blur_bg)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.MIRROR)
            blurBg.setRenderEffect(blurEffect)
            blurBg.setBackgroundColor(Color.parseColor("#80FFFFFF"))
        }

        tabList = listOf(
            findViewById(R.id.footer_skill_study),
            findViewById(R.id.footer_notes),
            findViewById(R.id.footer_ai),
            findViewById(R.id.footer_life),
            findViewById(R.id.footer_settings)
        )

        tabList.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                // 如果点击的就是当前已选中的，则不处理
                if (currentSelectedIndex != index) {
                    // 这里直接调用 updateState，由外部控制 ViewPager 切换
                    onTabSelectedListener?.invoke(index)
                }
            }
        }

        setCurrentSelectedIndex(0) // 默认选中第一个
    }

    /**
     * 核心方法：供外部（如 ViewPager2）调用，只更新 UI，不触发 Listener
     */
    fun setCurrentSelectedIndex(index: Int) {
        if (index < 0 || index >= tabList.size) return
        currentSelectedIndex = index
        updateState(index)
    }

    private fun updateState(selectedIndex: Int) {
        tabList.forEachIndexed { index, layout ->
            // 这里会改变 XML 中 selector 状态
            layout.isSelected = (index == selectedIndex)

            // 如果你没有写 selector XML，也可以在这里手动改颜色
            // val icon = layout.getChildAt(0) as? ImageView
            // icon?.setColorFilter(if (index == selectedIndex) Color.GREEN else Color.GRAY)
        }
    }
}