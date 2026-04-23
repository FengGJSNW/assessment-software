package com.example.xiaomingassistant.util.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.xiaomingassistant.util.calc.dp
import com.google.android.material.textfield.TextInputEditText





// 让 dialog 的创建可以使用 lambda 表达式
inline fun Context.DialogBuilder(action: AlertDialog.Builder.() -> Unit): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.action()
    return builder.create()
}

// 输入框创建（带样式）
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