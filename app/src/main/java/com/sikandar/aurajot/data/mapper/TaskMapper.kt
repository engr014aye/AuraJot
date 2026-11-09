package com.sikandar.aurajot.data.mapper

import com.sikandar.aurajot.data.local.entity.ChecklistConverter
import com.sikandar.aurajot.data.local.entity.TaskEntity
import com.sikandar.aurajot.domain.model.PriorityLevel
import com.sikandar.aurajot.domain.model.TaskItem

fun TaskEntity.toDomain(): TaskItem {
    return TaskItem(
        id = id,
        title = title,
        notes = notes,
        isCompleted = isCompleted,
        priority = PriorityLevel.fromString(priority),
        dueDateMillis = dueDateMillis,
        category = category,
        checklist = ChecklistConverter.toList(checklistRaw),
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}

fun TaskItem.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        notes = notes,
        isCompleted = isCompleted,
        priority = priority.name,
        dueDateMillis = dueDateMillis,
        category = category,
        checklistRaw = ChecklistConverter.fromList(checklist),
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
