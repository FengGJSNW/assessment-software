package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.LoginActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.view.MySettingCard
import com.example.xiaomingassistant.util.dialog.showConfirmDialog
import com.example.xiaomingassistant.util.toast.showShortToast

class SettingsFragment : Fragment(R.layout.main_interface_settings) {

    private lateinit var sessionManager: SessionManager
    private lateinit var logOutCard: MySettingCard
    private lateinit var personalizedCard: MySettingCard
    private lateinit var nightCard: MySettingCard
    private lateinit var fontSizeCard: MySettingCard
    private lateinit var powerCard: MySettingCard
    private lateinit var aboutCard: MySettingCard

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        bindViews(view)
        setupListeners()
    }

    // 绑定设置页中的各个卡片
    private fun bindViews(view: View) {
        personalizedCard = view.findViewById(R.id.settings_card_personalized)
        nightCard = view.findViewById(R.id.settings_card_night)
        fontSizeCard = view.findViewById(R.id.settings_card_fontsize)
        powerCard = view.findViewById(R.id.settings_card_power)
        aboutCard = view.findViewById(R.id.settings_card_about)
        logOutCard = view.findViewById(R.id.settings_card_login)
    }

    // 统一注册设置页点击事件
    private fun setupListeners() {
        // 个性化
        personalizedCard.setOnClickListener {
            /* TODO */
        }

        // 权限
        powerCard.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireContext().packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                showShortToast("无法打开系统设置")
            }
        }

        // 关于
        aboutCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fenggjsnw.top/about"))
            startActivity(intent)
        }

        // 登录/注销逻辑
        logOutCard.setOnClickListener {
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

    // 预留登录态变化后的 UI 刷新入口
    private fun updateLoginCard() {
        logOutCard.alpha = 1f
    }

    // 注销前先进行确认
    private fun showLogoutDialog() {
        requireContext().showConfirmDialog(
            title = "注销账号",
            message = "确定注销当前账号吗？注销后需要重新登录。",
            positiveText = "注销"
        ) {
                sessionManager.clearLogin()
                startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                activity?.finish()
            }
    }
}
