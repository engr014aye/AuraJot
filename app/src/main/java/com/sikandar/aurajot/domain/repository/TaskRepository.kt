package com.sikandar.aurajot.domain.repository

import com.sikandar.aurajot.domain.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getActiveTasks(): Flow<List<TaskItem>>
    fun getDeletedTasks(): Flow<List<TaskItem>>
    suspend fun getTaskById(id: Long): TaskItem?
    suspend fun insertOrUpdateTask(task: TaskItem): Long
    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean)
    suspend fun softDeleteTask(id: Long, deletedAt: Long = System.currentTimeMillis())
    suspend fun restoreTask(id: Long)
    suspend fun permanentlyDeleteTask(id: Long)
    suspend fun emptyTaskTrash()
}
