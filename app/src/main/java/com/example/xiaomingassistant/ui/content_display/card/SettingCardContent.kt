package com.example.xiaomingassistant.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
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

        val titleView = view.findViewById<TextView>(R.id.functional_card_text_title)
        val summaryView = view.findViewById<TextView>(R.id.functional_card_summary)
        val iconView = view.findViewById<ImageView>(R.id.functional_card_icon)
        val arrowView = view.findViewById<ImageView>(R.id.functional_card_mode_arrow)
        switchBtn = view.findViewById(R.id.functional_card_mode_switch)
        val sliderGroup = view.findViewById<LinearLayout>(R.id.functional_card_mode_slider_group)
        slider = view.findViewById(R.id.functional_card_slider)
        val tvMin = view.findViewById<TextView>(R.id.functional_card_tv_min)
        val tvMax = view.findViewById<TextView>(R.id.functional_card_tv_max)
        val bottomSpace = view.findViewById<Space>(R.id.functional_card_bottom_space)
        val backgroundImageView = view.findViewById<ImageView>(R.id.functional_card_background_image)

        val title = ta.getString(R.styleable.MySettingCard_customTitle)
        val summary = ta.getString(R.styleable.MySettingCard_customText)
        val imageRes = ta.getResourceId(R.styleable.MySettingCard_customImage, 0)
        val mode = ta.getInt(R.styleable.MySettingCard_customMode, 0)
        val minLabel = ta.getString(R.styleable.MySettingCard_minLabel)
        val maxLabel = ta.getString(R.styleable.MySettingCard_maxLabel)
        val bottomSpaceValue = ta.getString(R.styleable.MySettingCard_bottomSpace)
        val imageResId = ta.getResourceId(R.styleable.MySettingCard_backgroundImage, 0)
        val titleColor = ta.getColor(R.styleable.MySettingCard_titleColor, titleView.currentTextColor)
        val summaryColor = ta.getColor(R.styleable.MySettingCard_summaryColor, summaryView.currentTextColor)
        val iconTintColor = ta.getColor(R.styleable.MySettingCard_iconTintColor, 0)
        val backgroundAlpha = ta.getFloat(R.styleable.MySettingCard_backgroundAlpha, 1f)

        // 背景图标设置
        if (imageResId != 0) {
            backgroundImageView.setImageResource(imageResId)
            backgroundImageView.isVisible = true
            backgroundImageView.alpha = backgroundAlpha.coerceIn(0f, 1f)
        } else {
            backgroundImageView.isVisible = false
        }
        if (imageRes != 0) {
            iconView.setImageResource(imageRes)
        }

        // 标题和介绍
        titleView.text = title ?: ""
        summaryView.text = summary ?: ""

        titleView.setTextColor(titleColor)
        summaryView.setTextColor(summaryColor)

        if (iconTintColor != 0) {
            iconView.setColorFilter(iconTintColor)
            arrowView.setColorFilter(iconTintColor)
        } else {
            iconView.clearColorFilter()
            arrowView.clearColorFilter()
        }

        backgroundImageView.alpha = backgroundAlpha.coerceIn(0f, 1f)

        // 按钮设置
        switchBtn?.apply {
            showText = false
            textOn = ""
            textOff = ""
        }

        // 底部空间填充
        bottomSpace.layoutParams.height = dp2px(bottomSpaceValue ?: "0") + dp2px("16")

        // 卡片模式切换
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

    private fun dp2px(dpString: String): Int {
        return try {
            val dpValue = dpString.toFloat()
            val density = resources.displayMetrics.density
            (dpValue * density + 0.5f).toInt()
        } catch (e: Exception) {
            // 如果转换失败（比如字符串不是数字），返回默认值 30dp 转换后的 px
            val defaultDensity = resources.displayMetrics.density
            (30f * defaultDensity + 0.5f).toInt()
        }
    }
}