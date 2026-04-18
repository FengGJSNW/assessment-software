package com.example.xiaomingassistant.data.ai

data class AiUiMessage(
    val role: String,   // user / assistant / system
    val content: String,
    val isLoading: Boolean = false
)