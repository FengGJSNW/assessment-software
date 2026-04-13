package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_registration_interface)
    }
}