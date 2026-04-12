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
        setContentView(R.layout.main_interface)

        // 全面屏设置
        WindowCompat.setDecorFitsSystemWindows(window, false) // 关闭系统自动避开状态栏、导航栏
        val controller = WindowCompat.getInsetsController(window, window.decorView)  // 获取一个系统栏控制器
        controller.isAppearanceLightStatusBars = true // 启动深色


        val viewPager = findViewById<ViewPager2>(R.id.main_view_pager)
        // 底栏
        val bottomBar = findViewById<MainInterfaceBottomBar>(R.id.main_bottom_bar)
        val bottomBlur = findViewById<RealtimeBlurView>(R.id.bottom_blur_view)

        // 设置 ViewPager 适配器
        val adapter = ViewPagesAdapter(this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4

        // 底栏对 ViewPages 的切换控制
        bottomBar.onTabSelectedListener = { index ->
            viewPager.setCurrentItem(index, true)
        }
        // 底栏图标颜色控制
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomBar.setCurrentSelectedIndex(position)
            }
        })

        // 处理底部系统导航栏
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updateLayoutParams {
                height = dpToPx(60f) + navInsets.bottom
            }

            val container = v.findViewById<LinearLayout>(R.id.bottom_nav_container)
            container?.updatePadding(bottom = navInsets.bottom)

            insets
        }

        // 底栏模糊层设强行重设置关键参数
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