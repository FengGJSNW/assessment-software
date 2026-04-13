package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.LoginActivity
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
        val loginCard = view.findViewById<MySettingCard>(R.id.settings_card_login)

        // 示例：点击跳转类卡片
        personalizedCard.setOnClickListener {
            // TODO: 打开个性化页面
        }

        powerCard.setOnClickListener {
            // TODO: 打开权限设置页面
        }

        aboutCard.setOnClickListener {
            // TODO: 打开关于页面
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://fenggjsnw.top/about")
            startActivity(intent)
        }

        loginCard.setOnClickListener {
            // TODO: 打开登陆界面
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

    }
}