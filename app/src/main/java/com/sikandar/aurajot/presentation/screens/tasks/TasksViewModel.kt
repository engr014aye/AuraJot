package com.sikandar.aurajot.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sikandar.aurajot.domain.model.ChecklistItem
import com.sikandar.aurajot.domain.model.PriorityLevel
import com.sikandar.aurajot.domain.model.TaskItem
import com.sikandar.aurajot.domain.usecase.GetActiveTasksUseCase
import com.sikandar.aurajot.domain.usecase.GetTaskByIdUseCase
import com.sikandar.aurajot.domain.usecase.RestoreTaskUseCase
import com.sikandar.aurajot.domain.usecase.SaveTaskUseCase
import com.sikandar.aurajot.domain.usecase.SoftDeleteTaskUseCase
import com.sikandar.aurajot.domain.usecase.ToggleTaskCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Done"),
    HIGH_PRIORITY("High"),
    TODAY("Today")
}

data class TasksUiState(
    val tasks: List<TaskItem> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val completionPercentage: Float = 0f,
    val isLoading: Boolean = false
)

class TasksViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val softDeleteTaskUseCase: SoftDeleteTaskUseCase,
    private val restoreTaskUseCase: RestoreTaskUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter

    val uiState: StateFlow<TasksUiState> = combine(
        getActiveTasksUseCase(),
        _filter
    ) { allTasks, currentFilter ->
        val total = allTasks.size
        val completed = allTasks.count { it.isCompleted }
        val percentage = if (total > 0) completed.toFloat() / total.toFloat() else 0f

        val filtered = when (currentFilter) {
            TaskFilter.ALL -> allTasks
            TaskFilter.ACTIVE -> allTasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> allTasks.filter { it.isCompleted }
            TaskFilter.HIGH_PRIORITY -> allTasks.filter { it.priority == PriorityLevel.HIGH }
            TaskFilter.TODAY -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                val endOfDay = cal.timeInMillis
                allTasks.filter { it.dueDateMillis != null && it.dueDateMillis <= endOfDay }
            }
        }

        TasksUiState(
            tasks = filtered,
            filter = currentFilter,
            completedCount = completed,
            totalCount = total,
            completionPercentage = percentage,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState(isLoading = true)
    )

    fun onFilterSelected(filter: TaskFilter) {
        _filter.value = filter
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(task.id, !task.isCompleted)
        }
    }

    fun toggleSubtask(task: TaskItem, subtaskId: String) {
        viewModelScope.launch {
            val updatedChecklist = task.checklist.map { item ->
                if (item.id == subtaskId) item.copy(isDone = !item.isDone) else item
            }
            val allDone = updatedChecklist.isNotEmpty() && updatedChecklist.all { it.isDone }
            val updatedTask = task.copy(
                checklist = updatedChecklist,
                isCompleted = if (allDone) true else task.isCompleted,
                updatedAt = System.currentTimeMillis()
            )
            saveTaskUseCase(updatedTask)
        }
    }

    fun saveTask(task: TaskItem) {
        viewModelScope.launch {
            saveTaskUseCase(task)
        }
    }

    fun softDelete(task: TaskItem) {
        viewModelScope.launch {
            softDeleteTaskUseCase(task.id)
        }
    }

    fun restore(task: TaskItem) {
        viewModelScope.launch {
            restoreTaskUseCase(task.id)
        }
    }
}
