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
            put(AppDatabaseHelper.COL_USERNAME, cleanUsername)
            put(AppDatabaseHelper.COL_PASSWORD, cleanPassword)
        }

        val rowId = db.insert(AppDatabaseHelper.TABLE_USER, null, values)
        db.close()

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

        val cursor = db.query(
            AppDatabaseHelper.TABLE_USER,
            arrayOf(AppDatabaseHelper.COL_USER_ID),
            "${AppDatabaseHelper.COL_USERNAME} = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()
        db.close()

        return exists
    }

    fun findUserByUsername(username: String): User? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            AppDatabaseHelper.TABLE_USER,
            arrayOf(
                AppDatabaseHelper.COL_USER_ID,
                AppDatabaseHelper.COL_USERNAME,
                AppDatabaseHelper.COL_PASSWORD
            ),
            "${AppDatabaseHelper.COL_USERNAME} = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val user = if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USERNAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_PASSWORD))

            User(
                id = id,
                username = name,
                password = password
            )
        } else {
            null
        }

        cursor.close()
        db.close()

        return user
    }
}