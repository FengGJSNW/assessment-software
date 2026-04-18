package com.example.xiaomingassistant.data.model

data class NoteCategory(
    val id: Long,
    val name: String,
    val createdAt: Long
)

data class NoteItem(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)