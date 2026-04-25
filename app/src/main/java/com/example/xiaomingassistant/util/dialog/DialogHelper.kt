package com.example.xiaomingassistant.util.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.xiaomingassistant.util.calc.dp
import com.example.xiaomingassistant.util.dialog.style.applyRoundedStyle
import com.google.android.material.textfield.TextInputEditText

// 让 dialog 的创建可以直接写成 lambda 风格
inline fun Context.DialogBuilder(action: AlertDialog.Builder.() -> Unit): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.action()
    return builder.create()
}

// 快速弹出统一样式的确认弹窗
fun Context.showConfirmDialog(
    title: String,
    message: String,
    positiveText: String = "确定",
    negativeText: String = "取消",
    onNegative: (() -> Unit)? = null,
    onPositive: (() -> Unit)? = null
): AlertDialog {
    val dialog = DialogBuilder {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
        setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
    }

    dialog.applyRoundedStyle()
    return dialog
}

// 为输入型弹窗创建统一内边距的输入框
fun AlertDialog.Builder.setInputView(
    initialText: String,
    hint: String = ""
): TextInputEditText {
    val context = this.context
    val editText = TextInputEditText(context).apply {
        setText(initialText)
        setHint(hint)
        setPadding(16.dp, 16.dp, 16.dp, 16.dp)
    }
    setView(editText)
    return editText
}
