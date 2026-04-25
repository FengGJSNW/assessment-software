package com.example.xiaomingassistant.data.model

data class Plan(
    val id: Long = 0,
    val userId: Long = 0,
    val title: String,
    val startDate: String,
    val endDate: String,
    val startTime: String = "",
    val endTime: String = "",
    val note: String,
    val isFinished: Int = 0
)
