package com.example.xiaomingassistant.data.model

data class ChatMessage(
    val role: String,   // system / user / assistant
    val content: String
)