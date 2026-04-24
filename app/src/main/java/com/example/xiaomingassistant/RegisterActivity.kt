package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.xiaomingassistant.data.repository.UserRepository
import com.example.xiaomingassistant.util.toast.showShortToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : BaseActivity() {

    // 组件 lazy 绑定
    private val passwordEdit by lazy { findViewById<TextInputEditText>(R.id.register_text_password) }
    private val confirmEdit by lazy { findViewById<TextInputEditText>(R.id.register_text_comfirm_password) }
    private val accountEdit by lazy { findViewById<TextInputEditText>(R.id.register_text_account) }
    private val registerBtn by lazy { findViewById<MaterialButton>(R.id.account_btn_register) }
    // 数据库
    private val userRepository by lazy { UserRepository(this) }

    override fun requiresLoginCheck(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_registration_interface)

        registerBtn.setOnClickListener {
            handleRegistration()
        }
    }

    private fun handleRegistration() {
        val username = accountEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        val confirmPassword = confirmEdit.text.toString()

        when {
            username.isEmpty() -> {
                showShortToast(this, "用户名不能为空")
                return
            }
            password.isEmpty() -> {
                showShortToast(this, "密码不能为空")
                return
            }
            password != confirmPassword -> {
                showShortToast(this, "两次输入的密码不一致")
                return
            }
        }

        val result = userRepository.register(username, password)

        val message = when (result) {
            UserRepository.RegisterResult.Success -> "注册成功"
            UserRepository.RegisterResult.UsernameEmpty -> "用户名不能为空"
            UserRepository.RegisterResult.PasswordEmpty -> "密码不能为空"
            UserRepository.RegisterResult.UsernameAlreadyExists -> "用户名已存在"
            UserRepository.RegisterResult.UnknownError -> "注册失败"
        }

        showShortToast(this, message)

        if (result == UserRepository.RegisterResult.Success) {
            finish()
        }
    }
}