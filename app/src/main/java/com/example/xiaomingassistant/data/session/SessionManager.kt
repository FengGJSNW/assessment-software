package com.example.xiaomingassistant.data.session

import android.content.Context

class SessionManager(context: Context) {

    private val sp = context.applicationContext
        .getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveLogin(userId: Long, username: String) {
        sp.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return sp.getBoolean(KEY_IS_LOGGED_IN, false) &&
                sp.getLong(KEY_USER_ID, -1L) != -1L
    }

    fun getUserId(): Long {
        return sp.getLong(KEY_USER_ID, -1L)
    }

    fun getUsername(): String {
        return sp.getString(KEY_USERNAME, "") ?: ""
    }

    fun clearLogin() {
        sp.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
    }
}