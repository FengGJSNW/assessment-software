package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.xiaomingassistant.ui.component.MainInterfaceBottomBar
import com.example.xiaomingassistant.ui.fragment.mainactivity.AiFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.LifeFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.NotesFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.SettingsFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.SkillStudyFragment

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_interface)

        val bottomBar = findViewById<MainInterfaceBottomBar>(R.id.main_bottom_bar)

        // 默认显示第一个 Fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, SkillStudyFragment())
                .commit()
        }

// 绑定底栏的点击逻辑
        bottomBar.onTabSelectedListener = { index ->
            // 关键点：显式声明类型为 Fragment (注意首字母大写)
            val fragment: androidx.fragment.app.Fragment = when (index) {
                0 -> SkillStudyFragment()
                1 -> NotesFragment()
                2 -> AiFragment()
                3 -> LifeFragment()
                4 -> SettingsFragment()
                else -> SkillStudyFragment()
            }

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, fragment)
                .commit()
        }
    }
}
