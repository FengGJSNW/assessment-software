package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.xiaomingassistant.data.repository.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : BaseActivity() {

    override fun requiresLoginCheck(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_login_interface)

        val passwordEditText = findViewById<TextInputEditText>(R.id.login_text_password)
        val accountEditText = findViewById<TextInputEditText>(R.id.login_text_account)
        val registerButton = findViewById<TextView>(R.id.login_text_register)
        val loginButton = findViewById<MaterialButton>(R.id.login_btn_login)

        val userRepository = UserRepository(this)

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            val username = accountEditText.text?.toString()?.trim().orEmpty()
            val password = passwordEditText.text?.toString().orEmpty()

            when (val result = userRepository.login(username, password)) {
                is UserRepository.LoginResult.Success -> {
                    sessionManager.saveLogin(result.user.id, result.user.username)
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                UserRepository.LoginResult.UsernameEmpty -> {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show()
                }

                UserRepository.LoginResult.PasswordEmpty -> {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show()
                }

                UserRepository.LoginResult.UserNotFound -> {
                    Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show()
                }

                UserRepository.LoginResult.WrongPassword -> {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}