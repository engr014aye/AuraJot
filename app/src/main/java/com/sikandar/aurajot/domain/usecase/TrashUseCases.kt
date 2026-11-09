package com.sikandar.aurajot.domain.usecase

import com.sikandar.aurajot.domain.model.TrashItem
import com.sikandar.aurajot.domain.repository.NoteRepository
import com.sikandar.aurajot.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetTrashItemsUseCase(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<TrashItem>> {
        return combine(
            noteRepository.getDeletedNotes(),
            taskRepository.getDeletedTasks()
        ) { notes, tasks ->
            val noteTrash = notes.map { TrashItem.NoteTrash(it) }
            val taskTrash = tasks.map { TrashItem.TaskTrash(it) }
            (noteTrash + taskTrash).sortedByDescending { it.deletedAt }
        }
    }
}

class RestoreTrashItemUseCase(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(item: TrashItem) {
        when (item) {
            is TrashItem.NoteTrash -> noteRepository.restoreNote(item.id)
            is TrashItem.TaskTrash -> taskRepository.restoreTask(item.id)
        }
    }
}

class PermanentlyDeleteTrashItemUseCase(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(item: TrashItem) {
        when (item) {
            is TrashItem.NoteTrash -> noteRepository.permanentlyDeleteNote(item.id)
            is TrashItem.TaskTrash -> taskRepository.permanentlyDeleteTask(item.id)
        }
    }
}

class EmptyAllTrashUseCase(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke() {
        noteRepository.emptyNoteTrash()
        taskRepository.emptyTaskTrash()
    }
}
