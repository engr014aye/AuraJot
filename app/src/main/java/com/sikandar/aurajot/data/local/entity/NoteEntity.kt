package com.sikandar.aurajot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val colorHex: String,
    val isPinned: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
)
