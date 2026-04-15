package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.WeatherActivity
import com.example.xiaomingassistant.ui.view.MySettingCard

class LifeFragment : Fragment(R.layout.main_interface_life) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gotoWeather = view.findViewById<MySettingCard>(R.id.life_card_weather)

        gotoWeather.setOnClickListener {
            val intent = Intent(requireContext(), WeatherActivity::class.java)
            startActivity(intent)
        }


    }
}