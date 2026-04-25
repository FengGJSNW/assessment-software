package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.xiaomingassistant.data.repository.UserRepository
import com.example.xiaomingassistant.util.toast.showShortToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : BaseActivity() {

    private lateinit var passwordEdit: TextInputEditText
    private lateinit var confirmEdit: TextInputEditText
    private lateinit var accountEdit: TextInputEditText
    private lateinit var registerBtn: MaterialButton

    // 数据库
    private val userRepository by lazy { UserRepository(this) }

    override fun requiresLoginCheck(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_registration_interface)

        bindViews()
        setupListeners()
    }

    // 绑定注册页组件
    private fun bindViews() {
        passwordEdit = findViewById(R.id.register_text_password)
        confirmEdit = findViewById(R.id.register_text_comfirm_password)
        accountEdit = findViewById(R.id.register_text_account)
        registerBtn = findViewById(R.id.account_btn_register)
    }

    // 绑定注册按钮事件
    private fun setupListeners() {
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
                showShortToast("用户名不能为空")
                return
            }
            password.isEmpty() -> {
                showShortToast("密码不能为空")
                return
            }
            password != confirmPassword -> {
                showShortToast("两次输入的密码不一致")
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

        showShortToast(message)

        if (result == UserRepository.RegisterResult.Success) {
            finish()
        }
    }
}
