package com.example.xiaomingassistant.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.xiaomingassistant.ui.fragment.main_activity.AiFragment
import com.example.xiaomingassistant.ui.fragment.main_activity.LifeFragment
import com.example.xiaomingassistant.ui.fragment.main_activity.NotesFragment
import com.example.xiaomingassistant.ui.fragment.main_activity.SettingsFragment
import com.example.xiaomingassistant.ui.fragment.main_activity.SkillStudyFragment

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