package com.example.xiaomingassistant.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.xiaomingassistant.ui.fragment.mainactivity.AiFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.LifeFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.NotesFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.SettingsFragment
import com.example.xiaomingassistant.ui.fragment.mainactivity.SkillStudyFragment

class ViewPagesAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5 // 总共 5 个界面

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SkillStudyFragment()
            1 -> NotesFragment()
            2 -> AiFragment()
            3 -> LifeFragment()
            4 -> SettingsFragment()
            else -> SkillStudyFragment()
        }
    }
}