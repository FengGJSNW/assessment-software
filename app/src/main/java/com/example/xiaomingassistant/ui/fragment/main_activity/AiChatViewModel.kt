package com.example.xiaomingassistant.ui.fragment.main_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaomingassistant.data.ai.AiChatRepository
import com.example.xiaomingassistant.data.ai.AiSessionStorage
import com.example.xiaomingassistant.data.ai.AiUiMessage
import com.example.xiaomingassistant.data.ai.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AiChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AiChatRepository()
    private val storage = AiSessionStorage(application)

    private val systemPrompt = ChatMessage(
        role = "system",
        content = "你是小明助手 App 中的 AI 学习助手。请用中文回答，表达清晰，适合 Android/Kotlin 学习场景。"
    )

    private val conversation = mutableListOf<ChatMessage>()

    private val _uiMessages = MutableStateFlow<List<AiUiMessage>>(emptyList())
    val uiMessages: StateFlow<List<AiUiMessage>> = _uiMessages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    init {
        val saved = storage.loadConversation()

        if (saved.isEmpty()) {
            conversation.add(systemPrompt)
            _uiMessages.value = listOf(
                AiUiMessage(
                    role = "assistant",
                    content = "你好，我是你的 AI 助手。你可以问我 Kotlin、Android、Fragment、SQLite、UI 设计等问题。"
                )
            )
        } else {
            conversation.addAll(saved)
            _uiMessages.value = saved
                .filter { it.role != "system" }
                .map {
                    AiUiMessage(
                        role = it.role,
                        content = it.content
                    )
                }
        }
    }

    fun sendMessage(text: String) {
        val userText = text.trim()
        if (userText.isBlank() || _isSending.value) return

        val userMessage = ChatMessage("user", userText)
        conversation.add(userMessage)
        storage.saveConversation(conversation)

        _uiMessages.value = _uiMessages.value +
                AiUiMessage(role = "user", content = userText) +
                AiUiMessage(role = "assistant", content = "正在思考中，复杂问题可能需要更久一点…", isLoading = true)

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
                storage.saveConversation(conversation)

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

            } catch (e: SocketTimeoutException) {
                replaceLoadingWithError("AI 回复时间较长，请稍后再试，或换一个更简短的问题。")

            } catch (e: UnknownHostException) {
                replaceLoadingWithError("当前网络不可用，请检查网络连接。")

            } catch (e: IOException) {
                replaceLoadingWithError("网络连接异常，请稍后重试。")

            } catch (e: Exception) {
                replaceLoadingWithError("发生异常：${e.message ?: "未知错误"}")

            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearConversation() {
        conversation.clear()
        conversation.add(systemPrompt)
        storage.clearConversation()

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