package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_login_interface)

        val passwordEditText = findViewById<TextView>(R.id.login_text_password)
        val accountEditText = findViewById<TextView>(R.id.login_text_account)
        val registerButton = findViewById<TextView>(R.id.login_text_register)
        val loginButton = findViewById<TextView>(R.id.login_btn_login)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}