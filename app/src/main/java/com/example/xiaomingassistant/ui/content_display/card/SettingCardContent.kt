package com.example.xiaomingassistant.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.xiaomingassistant.R
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider

class MySettingCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var switchBtn: MaterialSwitch? = null
    var slider: Slider? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.myview_inner_display_card_for_settings,
            this,
            true
        )

        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.MySettingCard,
            defStyleAttr,
            0
        )

        val title = ta.getString(R.styleable.MySettingCard_customTitle)
        val summary = ta.getString(R.styleable.MySettingCard_customText)
        val imageRes = ta.getResourceId(R.styleable.MySettingCard_customImage, 0)
        val mode = ta.getInt(R.styleable.MySettingCard_customMode, 0)
        val minLabel = ta.getString(R.styleable.MySettingCard_minLabel)
        val maxLabel = ta.getString(R.styleable.MySettingCard_maxLabel)

        val titleView = view.findViewById<TextView>(R.id.internal_title)
        val summaryView = view.findViewById<TextView>(R.id.internal_summary)
        val iconView = view.findViewById<ImageView>(R.id.internal_icon)
        val arrowView = view.findViewById<ImageView>(R.id.mode_arrow)
        switchBtn = view.findViewById(R.id.mode_switch)
        val sliderGroup = view.findViewById<LinearLayout>(R.id.mode_slider_group)
        slider = view.findViewById(R.id.internal_slider)
        val tvMin = view.findViewById<TextView>(R.id.tv_min)
        val tvMax = view.findViewById<TextView>(R.id.tv_max)

        titleView.text = title ?: ""
        summaryView.text = summary ?: ""

        if (imageRes != 0) {
            iconView.setImageResource(imageRes)
        }

        switchBtn?.apply {
            showText = false
            textOn = ""
            textOff = ""
        }

        when (mode) {
            0 -> { // jump
                arrowView.isVisible = true
                switchBtn?.isVisible = false
                sliderGroup.isVisible = false
            }

            1 -> { // toggle
                arrowView.isVisible = false
                switchBtn?.isVisible = true
                sliderGroup.isVisible = false
            }

            2 -> { // slider
                arrowView.isVisible = false
                switchBtn?.isVisible = false
                sliderGroup.isVisible = true
                tvMin.text = minLabel ?: "0"
                tvMax.text = maxLabel ?: "100"
            }

            else -> {
                arrowView.isVisible = true
                switchBtn?.isVisible = false
                sliderGroup.isVisible = false
            }
        }

        ta.recycle()

        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
        isClickable = true
        isFocusable = true
    }
}