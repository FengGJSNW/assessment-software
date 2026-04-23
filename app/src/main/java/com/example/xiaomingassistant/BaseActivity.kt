package com.example.xiaomingassistant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.xiaomingassistant.data.session.SessionManager

open class BaseActivity : AppCompatActivity() {

    protected lateinit var sessionManager: SessionManager
    protected var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)


        sessionManager = SessionManager(this)
        currentUserId = sessionManager.getUserId()
    }

    override fun onStart() {
        super.onStart()

        if (requiresLoginCheck() && !sessionManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    protected open fun requiresLoginCheck(): Boolean = true

    protected fun logoutAndGoLogin() {
        sessionManager.clearLogin()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }85

    fun styleDialog(dialog: AlertDialog) {
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)

        val textColor = getColor(R.color.black)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(textColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(textColor)
    }
}

object ActivityCollector {

    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }
}