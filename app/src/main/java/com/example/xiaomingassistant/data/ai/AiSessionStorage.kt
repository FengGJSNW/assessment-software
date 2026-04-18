package com.example.xiaomingassistant.data.ai

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AiSessionStorage(context: Context) {

    private val sp = context.getSharedPreferences("ai_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveConversation(messages: List<ChatMessage>) {
        val json = gson.toJson(messages)
        sp.edit().putString(KEY_CONVERSATION, json).apply()
    }

    fun loadConversation(): List<ChatMessage> {
        val json = sp.getString(KEY_CONVERSATION, null) ?: return emptyList()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun clearConversation() {
        sp.edit().remove(KEY_CONVERSATION).apply()
    }

    companion object {
        private const val KEY_CONVERSATION = "conversation"
    }
}