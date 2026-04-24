package com.example.xiaomingassistant.data.repository

import com.example.xiaomingassistant.data.model.ChatMessage
import com.example.xiaomingassistant.network.deepseek.DeepSeekChatRequest
import com.example.xiaomingassistant.network.deepseek.DeepSeekMessage
import com.example.xiaomingassistant.network.deepseek.DeepSeekNetwork

class AiChatRepository {

    suspend fun sendMessage(messages: List<ChatMessage>): String {
        val request = DeepSeekChatRequest(
            model = "deepseek-chat",
            messages = messages.map {
                DeepSeekMessage(
                    role = it.role,
                    content = it.content
                )
            },
            stream = false
        )

        val response = DeepSeekNetwork.api.chatCompletion(request)
        return response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
    }
}