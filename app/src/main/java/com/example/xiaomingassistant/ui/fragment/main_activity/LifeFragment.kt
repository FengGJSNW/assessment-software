package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.WeatherActivity
import com.example.xiaomingassistant.ui.view.MySettingCard

class LifeFragment : Fragment(R.layout.main_interface_life) {

    private lateinit var weatherCard: MySettingCard

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupListeners()
    }

    // 绑定生活页组件
    private fun bindViews(view: View) {
        weatherCard = view.findViewById(R.id.life_card_weather)
    }

    // 绑定天气入口
    private fun setupListeners() {
        weatherCard.setOnClickListener {
            val intent = Intent(requireContext(), WeatherActivity::class.java)
            startActivity(intent)
        }
    }
}
