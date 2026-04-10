package com.example.xiaomingassistant

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.viewpager2.widget.ViewPager2
import com.example.xiaomingassistant.ui.adapter.ViewPagesAdapter
import com.example.xiaomingassistant.ui.component.MainInterfaceBottomBar
import com.example.xiaomingassistant.ui.view.RealtimeBlurView.RealtimeBlurView

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

        setContentView(R.layout.main_interface)


        // 1. 开启真正的全屏沉浸式布局
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.main_interface)

        val bottomBar = findViewById<MainInterfaceBottomBar>(R.id.main_bottom_bar)
        val viewPager = findViewById<ViewPager2>(R.id.main_view_pager)
        // 关键：在这里寻找模糊 View
        val bottomBlur = findViewById<RealtimeBlurView>(R.id.bottom_blur_view)

        // 2. 设置 ViewPager 适配器
        val adapter = ViewPagesAdapter(this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4

        // 3. 联动逻辑：底栏控制 ViewPager
        bottomBar.onTabSelectedListener = { index ->
            viewPager.setCurrentItem(index, true)
        }

        // 4. 联动逻辑：ViewPager 控制底栏图标高亮
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomBar.setCurrentSelectedIndex(position)
            }
        })

        // 5. 沉浸式适配：处理底部系统导航栏
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            v.updateLayoutParams {
                // 60dp 是你在 XML 中定义的基础高度
                height = dpToPx(60f) + navInsets.bottom
            }

            val container = v.findViewById<LinearLayout>(R.id.bottom_nav_container)
            container?.updatePadding(bottom = navInsets.bottom)

            insets
        }

        // 6. 强制刷新模糊 View 的参数（放在 onCreate 内部）
        bottomBlur?.post {
            bottomBlur.setBlurRadius(dpToPx(5f).toFloat())
            bottomBlur.invalidate() // 强行触发重绘抓取背景
        }
    }

    // 工具函数：将 dp 转为 px
    private fun dpToPx(dp: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    ).toInt()
}