package com.example.xiaomingassistant.network.deepseek

data class DeepSeekMessage(
    val role: String,
    val content: String
)

data class DeepSeekChatRequest(
    val model: String,
    val messages: List<DeepSeekMessage>,
    val stream: Boolean = false
)

data class DeepSeekChatResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val message: DeepSeekMessage
    )
}