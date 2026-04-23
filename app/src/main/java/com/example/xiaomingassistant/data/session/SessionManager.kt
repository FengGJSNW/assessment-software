package com.example.xiaomingassistant.data.session

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {

    // 初始化存储对象
    private val sharedPreferences = context.applicationContext
        .getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // 存用户登陆状态
    fun saveLogin(userId: Long, username: String) {
        sharedPreferences.edit {
            putBoolean(KEY_IS_LOGGED_IN, true) // 登陆状态保存
            putLong(KEY_USER_ID, userId)       // 存放用户编号
            putString(KEY_USERNAME, username)  // 存放用户名
        }
    }

    // 清除登陆状态
    fun clearLogin() {
        sharedPreferences.edit().clear().apply()
    }

    // 获取登陆状态
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) &&
                sharedPreferences.getLong(KEY_USER_ID, -1L) != -1L
    }

    // 获取用户id
    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }

    // 获取用户名
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }


    // 数据存储键值
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
    }
}