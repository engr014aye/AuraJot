package com.sikandar.aurajot.domain.model

data class ChecklistItem(
    val id: String,
    val text: String,
    val isDone: Boolean = false
)
