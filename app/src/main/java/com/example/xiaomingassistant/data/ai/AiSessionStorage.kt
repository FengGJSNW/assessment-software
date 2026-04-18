package com.example.xiaomingassistant.data.ai

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AiSessionStorage(context: Context) {

    private val sp = context.getSharedPreferences("ai_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun keyConversation(userId: Long): String = "conversation_$userId"

    fun saveConversation(userId: Long, messages: List<ChatMessage>) {
        val json = gson.toJson(messages)
        sp.edit().putString(keyConversation(userId), json).apply()
    }

    fun loadConversation(userId: Long): List<ChatMessage> {
        val json = sp.getString(keyConversation(userId), null) ?: return emptyList()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun clearConversation(userId: Long) {
        sp.edit().remove(keyConversation(userId)).apply()
    }
}