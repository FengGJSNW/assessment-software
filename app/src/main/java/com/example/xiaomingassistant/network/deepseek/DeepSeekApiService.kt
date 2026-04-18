package com.example.xiaomingassistant.network.deepseek

import retrofit2.http.Body
import retrofit2.http.POST

interface DeepSeekApiService {

    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body request: DeepSeekChatRequest
    ): DeepSeekChatResponse
}