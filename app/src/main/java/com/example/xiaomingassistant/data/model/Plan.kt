package com.example.xiaomingassistant.data.model

data class Plan(
    val id: Long = 0,
    val title: String,
    val startDate: String,
    val endDate: String,
    val note: String,
    val isFinished: Int = 0 // 0=未完成 1=完成
)