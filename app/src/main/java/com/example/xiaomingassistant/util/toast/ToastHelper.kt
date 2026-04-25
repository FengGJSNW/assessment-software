package com.example.xiaomingassistant.util.toast

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlin.jvm.JvmName

// Context 场景下的短 Toast
fun showShortToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

// Context 场景下的长 Toast
fun showLongToast(context: Context,text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

// 让 Activity / Service 等 Context 调用更顺手
@JvmName("showShortToastForContext")
fun Context.showShortToast(text: String) {
    showShortToast(this, text)
}

// Fragment 内直接调用，少写一层 requireContext()
fun Fragment.showShortToast(text: String) {
    showShortToast(requireContext(), text)
}
