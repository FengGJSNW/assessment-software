package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.adapter.ViewPagesAdapter
import com.example.xiaomingassistant.ui.component.MainInterfaceBottomBar

class MainActivity : BaseActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomBar: MainInterfaceBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_interface)

        setupEdgeToEdge()
        bindViews()
        setupViewPager()
        setupBottomBar()
    }

    // 全面屏设置
    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
    }

    // 绑定主页组件
    private fun bindViews() {
        viewPager = findViewById(R.id.main_view_pager)
        bottomBar = findViewById(R.id.main_bottom_bar)
    }

    // 设置 ViewPager 适配器与页面监听
    private fun setupViewPager() {
        // 设置 ViewPager 适配器
        val adapter = ViewPagesAdapter(this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4

        // 页面切换时更新底栏选中状态
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomBar.setCurrentSelectedIndex(position)
            }
        })
    }

    // 底栏点击切页
    private fun setupBottomBar() {
        bottomBar.onTabSelectedListener = { index ->
            viewPager.setCurrentItem(index, true)
        }
    }

    override fun onStart() {
        super.onStart()

        val sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
