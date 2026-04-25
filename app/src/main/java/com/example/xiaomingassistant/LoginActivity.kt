package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.xiaomingassistant.data.repository.UserRepository
import com.example.xiaomingassistant.util.toast.showShortToast // 确保引用了你的工具类
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : BaseActivity() {

    private lateinit var accountEdit: TextInputEditText
    private lateinit var passwordEdit: TextInputEditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var registerText: TextView

    private val userRepository by lazy { UserRepository(this) }

    override fun requiresLoginCheck(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_login_interface)

        bindViews()
        setupListeners()
    }

    // 绑定登录页组件
    private fun bindViews() {
        accountEdit = findViewById(R.id.login_text_account)
        passwordEdit = findViewById(R.id.login_text_password)
        loginBtn = findViewById(R.id.login_btn_login)
        registerText = findViewById(R.id.login_text_register)
    }

    // 绑定按钮事件
    private fun setupListeners() {
        // 跳转注册
        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 执行登录
        loginBtn.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = accountEdit.text?.toString()?.trim().orEmpty()
        val password = passwordEdit.text?.toString().orEmpty()

        if (username.isEmpty()) {
            showShortToast("用户名不能为空")
            return
        }
        if (password.isEmpty()) {
            showShortToast("密码不能为空")
            return
        }

        // 执行登录逻辑
        val result = userRepository.login(username, password)

        val message = when (result) {
            is UserRepository.LoginResult.Success -> {
                // 执行登录成功后的持久化与跳转
                performLoginSuccess(result)
                "登录成功"
            }
            UserRepository.LoginResult.UsernameEmpty -> "用户名不能为空"
            UserRepository.LoginResult.PasswordEmpty -> "密码不能为空"
            UserRepository.LoginResult.UserNotFound -> "用户不存在"
            UserRepository.LoginResult.WrongPassword -> "密码错误"
        }

        showShortToast(message)
    }

    private fun performLoginSuccess(result: UserRepository.LoginResult.Success) {
        // 存储 Session
        sessionManager.saveLogin(result.user.id, result.user.username)

        // 跳转主页并清空任务栈（防止用户按返回键回到登录页）
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

}
