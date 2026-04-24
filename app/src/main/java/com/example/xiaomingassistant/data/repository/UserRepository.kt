package com.example.xiaomingassistant.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.xiaomingassistant.data.db.AppDatabaseHelper
import com.example.xiaomingassistant.data.model.User

class UserRepository(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    sealed class RegisterResult {
        data object Success : RegisterResult()
        data object UsernameEmpty : RegisterResult()
        data object PasswordEmpty : RegisterResult()
        data object UsernameAlreadyExists : RegisterResult()
        data object UnknownError : RegisterResult()
    }

    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        data object UsernameEmpty : LoginResult()
        data object PasswordEmpty : LoginResult()
        data object UserNotFound : LoginResult()
        data object WrongPassword : LoginResult()
    }

    fun register(username: String, password: String): RegisterResult {
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        if (cleanUsername.isEmpty()) {
            return RegisterResult.UsernameEmpty
        }

        if (cleanPassword.isEmpty()) {
            return RegisterResult.PasswordEmpty
        }

        if (isUsernameExists(cleanUsername)) {
            return RegisterResult.UsernameAlreadyExists
        }

        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("username", cleanUsername)
            put("password", cleanPassword)
        }

        // 使用 "user" 作为表名，对应 AppDatabaseHelper 中的表名
        val rowId = db.insert("user", null, values)

        return if (rowId != -1L) {
            RegisterResult.Success
        } else {
            RegisterResult.UnknownError
        }
    }

    fun login(username: String, password: String): LoginResult {
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        if (cleanUsername.isEmpty()) {
            return LoginResult.UsernameEmpty
        }

        if (cleanPassword.isEmpty()) {
            return LoginResult.PasswordEmpty
        }

        val user = findUserByUsername(cleanUsername) ?: return LoginResult.UserNotFound

        return if (user.password == cleanPassword) {
            LoginResult.Success(user)
        } else {
            LoginResult.WrongPassword
        }
    }

    fun isUsernameExists(username: String): Boolean {
        val db = dbHelper.readableDatabase

        // 这里的表名和列名均改为字符串硬编码
        val cursor = db.query(
            "user",
            arrayOf("id"),
            "username = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun findUserByUsername(username: String): User? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "user",
            arrayOf("id", "username", "password"),
            "username = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val user = if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))

            User(
                id = id,
                username = name,
                password = password
            )
        } else {
            null
        }

        cursor.close()
        return user
    }
}