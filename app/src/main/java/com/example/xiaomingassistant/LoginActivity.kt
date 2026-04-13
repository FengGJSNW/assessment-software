package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.xiaomingassistant.data.repository.UserRepository
import com.example.xiaomingassistant.data.session.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_login_interface)

        val passwordEditText = findViewById<TextInputEditText>(R.id.login_text_password)
        val accountEditText = findViewById<TextInputEditText>(R.id.login_text_account)
        val registerButton = findViewById<TextView>(R.id.login_text_register)
        val loginButton = findViewById<MaterialButton>(R.id.login_btn_login)

        val userRepository = UserRepository(this)
        val sessionManager = SessionManager(this)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = accountEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            when (val result = userRepository.login(username, password)) {
                is UserRepository.LoginResult.Success -> {
                    sessionManager.saveLogin(result.user.id, result.user.username)
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()

                    // 这里后面改成你的主页
                    // startActivity(Intent(this, MainActivity::class.java))
                    // finish()
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