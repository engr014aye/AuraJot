package com.sikandar.aurajot.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Personal",
    val colorHex: String = "#1E222D",
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
