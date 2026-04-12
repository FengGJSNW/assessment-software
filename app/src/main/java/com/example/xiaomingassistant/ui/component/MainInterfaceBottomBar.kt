package com.example.xiaomingassistant.ui.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.RealtimeBlurView.RealtimeBlurView

class MainInterfaceBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onTabSelectedListener: ((Int) -> Unit)? = null
    private lateinit var tabList: List<LinearLayout>
    private var currentSelectedIndex = 0

    private val blurView: RealtimeBlurView
    private val navContainer: LinearLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.main_interface_footer, this, true)

        setBackgroundColor(Color.TRANSPARENT)

        blurView = findViewById(R.id.bottom_blur_view)
        navContainer = findViewById(R.id.bottom_nav_container)

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

        // 组件内部自己处理导航栏 inset
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            v.updateLayoutParams {
                height = dpToPx(60f) + navInsets.bottom
            }

            navContainer.updatePadding(bottom = navInsets.bottom)

            insets
        }

        val blurRadius = resources.getDimension(R.dimen.global_blur_radius)
        blurView.setBlurRadius(blurRadius)
    }

    fun setCurrentSelectedIndex(index: Int) {
        if (index !in tabList.indices) return
        currentSelectedIndex = index
        updateState(index)
    }

    private fun updateState(selectedIndex: Int) {
        tabList.forEachIndexed { index, layout ->
            layout.isSelected = (index == selectedIndex)
        }
    }

    fun setBlurRadius(radius: Float) {
        blurView.setBlurRadius(radius)
        blurView.invalidate()
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}