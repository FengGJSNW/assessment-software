package com.example.xiaomingassistant.data.model

data class AiUiMessage(
    val role: String,   // user / assistant / system
    val content: String,
    val isLoading: Boolean = false
)