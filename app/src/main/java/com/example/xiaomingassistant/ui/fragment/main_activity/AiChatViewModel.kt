package com.example.xiaomingassistant.ui.fragment.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaomingassistant.data.ai.AiChatRepository
import com.example.xiaomingassistant.data.ai.AiUiMessage
import com.example.xiaomingassistant.data.ai.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AiChatViewModel : ViewModel() {

    private val repository = AiChatRepository()

    private val systemPrompt = ChatMessage(
        role = "system",
        content = "你是小明助手 App 中的 AI 学习助手。请用中文回答，表达清晰，适合 Android/Kotlin 学习场景。"
    )

    private val conversation = mutableListOf(systemPrompt)

    private val _uiMessages = MutableStateFlow<List<AiUiMessage>>(
        listOf(
            AiUiMessage(
                role = "assistant",
                content = "你好，我是你的 AI 助手。你可以问我 Kotlin、Android、Fragment、SQLite、UI 设计等问题。"
            )
        )
    )
    val uiMessages: StateFlow<List<AiUiMessage>> = _uiMessages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun sendMessage(text: String) {
        val userText = text.trim()
        if (userText.isBlank() || _isSending.value) return

        val userMessage = ChatMessage("user", userText)
        conversation.add(userMessage)

        _uiMessages.value = _uiMessages.value + AiUiMessage(
            role = "user",
            content = userText
        ) + AiUiMessage(
            role = "assistant",
            content = "正在思考...",
            isLoading = true
        )

        viewModelScope.launch {
            _isSending.value = true
            try {
                val reply = repository.sendMessage(conversation)

                val finalReply = if (reply.isBlank()) {
                    "我暂时没有生成有效回复，你可以稍后再试。"
                } else {
                    reply
                }

                conversation.add(ChatMessage("assistant", finalReply))

                val current = _uiMessages.value.toMutableList()
                if (current.isNotEmpty() && current.last().isLoading) {
                    current.removeAt(current.lastIndex)
                }
                current.add(AiUiMessage("assistant", finalReply))
                _uiMessages.value = current
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    401 -> "API Key 无效，请检查配置。"
                    402 -> "账户余额不足，暂时无法调用。"
                    429 -> "请求过于频繁，请稍后再试。"
                    500, 503 -> "DeepSeek 服务繁忙，请稍后再试。"
                    else -> "请求失败：${e.code()}"
                }
                replaceLoadingWithError(msg)
            } catch (e: Exception) {
                replaceLoadingWithError("网络异常：${e.message ?: "未知错误"}")
            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearConversation() {
        conversation.clear()
        conversation.add(systemPrompt)

        _uiMessages.value = listOf(
            AiUiMessage(
                role = "assistant",
                content = "已清空当前会话。现在我们可以开始新一轮对话。"
            )
        )
    }

    private fun replaceLoadingWithError(errorText: String) {
        val current = _uiMessages.value.toMutableList()
        if (current.isNotEmpty() && current.last().isLoading) {
            current.removeAt(current.lastIndex)
        }
        current.add(AiUiMessage("assistant", errorText))
        _uiMessages.value = current
    }
}