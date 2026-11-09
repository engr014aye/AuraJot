package com.sikandar.aurajot.domain.usecase

import com.sikandar.aurajot.domain.model.TaskItem
import com.sikandar.aurajot.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetActiveTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<TaskItem>> = repository.getActiveTasks()
}

class GetTaskByIdUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long): TaskItem? = repository.getTaskById(id)
}

class SaveTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: TaskItem): Long = repository.insertOrUpdateTask(task)
}

class ToggleTaskCompletionUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long, isCompleted: Boolean) =
        repository.toggleTaskCompletion(id, isCompleted)
}

class SoftDeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.softDeleteTask(id)
}

class RestoreTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.restoreTask(id)
}

class PermanentlyDeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.permanentlyDeleteTask(id)
}
