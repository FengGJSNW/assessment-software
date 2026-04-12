package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.MySettingCard
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView

class SettingsFragment : Fragment(R.layout.main_interface_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val personalizedCard = view.findViewById<MySettingCard>(R.id.settings_card_personalized)
        val nightCard = view.findViewById<MySettingCard>(R.id.settings_card_night)
        val fontSizeCard = view.findViewById<MySettingCard>(R.id.settings_card_fontsize)
        val powerCard = view.findViewById<MySettingCard>(R.id.settings_card_power)
        val aboutCard = view.findViewById<MySettingCard>(R.id.settings_card_about)

        // 示例：点击跳转类卡片
        personalizedCard.setOnClickListener {
            // TODO: 打开个性化页面
        }

        powerCard.setOnClickListener {
            // TODO: 打开权限设置页面
        }

        aboutCard.setOnClickListener {
            // TODO: 打开关于页面
        }

    }
}