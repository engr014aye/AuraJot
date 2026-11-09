package com.sikandar.aurajot.domain.model

data class TaskItem(
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val dueDateMillis: Long? = null,
    val category: String = "General",
    val checklist: List<ChecklistItem> = emptyList(),
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
