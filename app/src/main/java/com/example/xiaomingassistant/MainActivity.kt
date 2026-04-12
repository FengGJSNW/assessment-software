package com.example.xiaomingassistant

import android.os.Bundle
import android.util.TypedValue
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.xiaomingassistant.ui.adapter.ViewPagesAdapter
import com.example.xiaomingassistant.ui.component.MainInterfaceBottomBar

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_interface)

        // 全面屏设置
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

        val viewPager = findViewById<ViewPager2>(R.id.main_view_pager)
        val bottomBar = findViewById<MainInterfaceBottomBar>(R.id.main_bottom_bar)

        // 设置 ViewPager 适配器
        val adapter = ViewPagesAdapter(this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4

        // 底栏点击切页
        bottomBar.onTabSelectedListener = { index ->
            viewPager.setCurrentItem(index, true)
        }

        // 页面切换时更新底栏选中状态
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomBar.setCurrentSelectedIndex(position)
            }
        })

    }

    private fun dpToPx(dp: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    ).toInt()
}