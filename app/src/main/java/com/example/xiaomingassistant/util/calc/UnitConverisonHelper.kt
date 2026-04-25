package com.example.xiaomingassistant.util.calc

import android.content.res.Resources
import android.util.TypedValue

// Int 转 dp，适合直接给布局参数使用
val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

// Float 转 dp，适合需要保留小数输入的场景
val Float.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()
