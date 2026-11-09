package com.sikandar.aurajot.data.repository

import com.sikandar.aurajot.data.local.dao.TaskDao
import com.sikandar.aurajot.data.mapper.toDomain
import com.sikandar.aurajot.data.mapper.toEntity
import com.sikandar.aurajot.domain.model.TaskItem
import com.sikandar.aurajot.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao
) : TaskRepository {

    override fun getActiveTasks(): Flow<List<TaskItem>> {
        return dao.getActiveTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDeletedTasks(): Flow<List<TaskItem>> {
        return dao.getDeletedTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(id: Long): TaskItem? {
        return dao.getTaskById(id)?.toDomain()
    }

    override suspend fun insertOrUpdateTask(task: TaskItem): Long {
        return dao.insertOrUpdateTask(task.toEntity())
    }

    override suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) {
        dao.toggleTaskCompletion(id, isCompleted)
    }

    override suspend fun softDeleteTask(id: Long, deletedAt: Long) {
        dao.softDeleteTask(id, deletedAt)
    }

    override suspend fun restoreTask(id: Long) {
        dao.restoreTask(id)
    }

    override suspend fun permanentlyDeleteTask(id: Long) {
        dao.permanentlyDeleteTask(id)
    }

    override suspend fun emptyTaskTrash() {
        dao.emptyTrash()
    }
}
