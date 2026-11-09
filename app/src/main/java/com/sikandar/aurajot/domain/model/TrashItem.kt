package com.sikandar.aurajot.domain.model

sealed class TrashItem {
    abstract val id: Long
    abstract val title: String
    abstract val deletedAt: Long
    abstract val itemType: String

    data class NoteTrash(
        val note: Note
    ) : TrashItem() {
        override val id: Long get() = note.id
        override val title: String get() = note.title.ifBlank { "Untitled Note" }
        override val deletedAt: Long get() = note.deletedAt ?: note.updatedAt
        override val itemType: String get() = "Note"
    }

    data class TaskTrash(
        val task: TaskItem
    ) : TrashItem() {
        override val id: Long get() = task.id
        override val title: String get() = task.title.ifBlank { "Untitled Task" }
        override val deletedAt: Long get() = task.deletedAt ?: task.updatedAt
        override val itemType: String get() = "Task"
    }
}
