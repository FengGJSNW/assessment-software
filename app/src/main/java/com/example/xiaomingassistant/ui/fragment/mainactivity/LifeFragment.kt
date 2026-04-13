package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.LoginActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.activity.WeatherActivity
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