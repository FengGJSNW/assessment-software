package com.example.xiaomingassistant.data.ai

data class ChatMessage(
    val role: String,   // system / user / assistant
    val content: String
)