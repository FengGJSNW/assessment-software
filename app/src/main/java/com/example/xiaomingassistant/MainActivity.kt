package com.example.xiaomingassistant

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.xiaomingassistant.ui.component.MainInterfaceBottomBar
import com.example.xiaomingassistant.ui.fragment.mainactivity.AiFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.LifeFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.NotesFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.SettingsFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.SkillStudyFragment
import com.example.xiaomingassistant.ui.adapter.ViewPagesAdapter

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_interface)

        val bottomBar = findViewById<MainInterfaceBottomBar>(R.id.main_bottom_bar)
        val viewPager = findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.main_view_pager)

        // 1. 设置 ViewPager 适配器
        val adapter = ViewPagesAdapter(this)
        viewPager.adapter = adapter

        // 设置预加载数量，防止滑动时 Fragment 被销毁重建（尤其是含有复杂绘图的 SkillStudy）
        viewPager.offscreenPageLimit = 4

        // 2. 底栏点击控制 ViewPager 切换
        bottomBar.onTabSelectedListener = { index ->
            // second parameter 'false' means no smooth scroll animation
            // 如果你想要滑动的动画，可以设为 true
            viewPager.setCurrentItem(index, true)
        }

        // 3. ViewPager 滑动控制底栏高亮
        viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 假设你的 BottomBar 类里有一个方法可以手动设置选中的索引（不触发点击回调）
                // 比如叫 setCurrentSelectedIndex(position)
                bottomBar.setCurrentSelectedIndex(position)
            }
        })
    }
}
