package com.example.xiaomingassistant.util.dialog.style

import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.xiaomingassistant.R

/**
 * 应用圆角样式并统一按钮字体
 * @param textSizeSp 字体大小，单位为 SP，默认为 16sp
 * @param contextColor 正文字体颜色资源 ID，默认为黑色
 * @param buttonTextColor 按钮字体颜色，默认为黑色
 */
fun AlertDialog.applyRoundedStyle(
    textSizeSp: Float = 16f,
    contextColor: Int = R.color.black,
    buttonTextColor: Int = R.color.black
) {
    show()

    window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)

    val buttonTextColor = ContextCompat.getColor(context, buttonTextColor)

    listOf(
        AlertDialog.BUTTON_POSITIVE,
        AlertDialog.BUTTON_NEGATIVE,
        AlertDialog.BUTTON_NEUTRAL,
    ).forEach { buttonType ->
        getButton(buttonType)?.apply {
            setTextColor(buttonTextColor)
            textSize = textSizeSp
        }
    }
}