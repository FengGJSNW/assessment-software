package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.LoginActivity
import com.example.xiaomingassistant.MainActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.view.MySettingCard

class SettingsFragment : Fragment(R.layout.main_interface_settings) {

    private lateinit var sessionManager: SessionManager
    private lateinit var loginCard: MySettingCard

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val personalizedCard = view.findViewById<MySettingCard>(R.id.settings_card_personalized)
        val nightCard = view.findViewById<MySettingCard>(R.id.settings_card_night)
        val fontSizeCard = view.findViewById<MySettingCard>(R.id.settings_card_fontsize)
        val powerCard = view.findViewById<MySettingCard>(R.id.settings_card_power)
        val aboutCard = view.findViewById<MySettingCard>(R.id.settings_card_about)
        loginCard = view.findViewById(R.id.settings_card_login)

        personalizedCard.setOnClickListener {
            // TODO
        }

        powerCard.setOnClickListener {
            // TODO
        }

        aboutCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://fenggjsnw.top/about")
            startActivity(intent)
        }

        loginCard.setOnClickListener {
            if (sessionManager.isLoggedIn()) {
                showLogoutDialog()
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateLoginCard()
    }

    private fun updateLoginCard() {
        // 如果 MySettingCard 有 setter，就打开下面两行
        // loginCard.setTitle(if (sessionManager.isLoggedIn()) "账号" else "登陆和注册")
        // loginCard.setText(if (sessionManager.isLoggedIn()) "注销当前账号" else "登陆")

        loginCard.alpha = if (sessionManager.isLoggedIn()) 1f else 1f
    }

    private fun showLogoutDialog() {
        val activity = activity as? MainActivity ?: return

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("注销账号")
            .setMessage("确定注销当前账号吗？注销后需要重新登录。")
            .setPositiveButton("注销") { _, _ ->
                sessionManager.clearLogin()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity.finish()
            }
            .setNegativeButton("取消", null)
            .create()

        activity.styleDialog(dialog)
    }
}