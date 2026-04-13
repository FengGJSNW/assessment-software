package com.example.xiaomingassistant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.xiaomingassistant.data.repository.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_registration_interface)

        val passwordEditText = findViewById<TextInputEditText>(R.id.register_text_password)
        val passwordConfirmEditText = findViewById<TextInputEditText>(R.id.register_text_comfirm_password)
        val accountEditText = findViewById<TextInputEditText>(R.id.register_text_account)
        val registerButton = findViewById<MaterialButton>(R.id.account_btn_register)

        val userRepository = UserRepository(this)

        registerButton.setOnClickListener {
            val username = accountEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = passwordConfirmEditText.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (userRepository.register(username, password)) {
                UserRepository.RegisterResult.Success -> {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
                UserRepository.RegisterResult.UsernameEmpty -> {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show()
                }
                UserRepository.RegisterResult.PasswordEmpty -> {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show()
                }
                UserRepository.RegisterResult.UsernameAlreadyExists -> {
                    Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show()
                }
                UserRepository.RegisterResult.UnknownError -> {
                    Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}