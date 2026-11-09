package com.sikandar.aurajot.data.mapper

import com.sikandar.aurajot.data.local.entity.NoteEntity
import com.sikandar.aurajot.domain.model.Note

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        category = category,
        colorHex = colorHex,
        isPinned = isPinned,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        category = category,
        colorHex = colorHex,
        isPinned = isPinned,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
