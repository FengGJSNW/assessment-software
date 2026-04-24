package com.example.xiaomingassistant.util.toast

import android.content.Context
import android.widget.Toast
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.EditPlanFragment

fun showShortToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context,text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}