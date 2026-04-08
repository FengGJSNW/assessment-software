package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_interface)
    }
}