package com.sikandar.aurajot.presentation.screens.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sikandar.aurajot.domain.model.TrashItem
import com.sikandar.aurajot.domain.usecase.EmptyAllTrashUseCase
import com.sikandar.aurajot.domain.usecase.GetTrashItemsUseCase
import com.sikandar.aurajot.domain.usecase.PermanentlyDeleteTrashItemUseCase
import com.sikandar.aurajot.domain.usecase.RestoreTrashItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TrashFilter(val label: String) {
    ALL("All"),
    NOTES("Notes"),
    TASKS("To-Dos")
}

data class TrashUiState(
    val items: List<TrashItem> = emptyList(),
    val filter: TrashFilter = TrashFilter.ALL,
    val totalCount: Int = 0,
    val isLoading: Boolean = false
)

class TrashViewModel(
    private val getTrashItemsUseCase: GetTrashItemsUseCase,
    private val restoreTrashItemUseCase: RestoreTrashItemUseCase,
    private val permanentlyDeleteTrashItemUseCase: PermanentlyDeleteTrashItemUseCase,
    private val emptyAllTrashUseCase: EmptyAllTrashUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(TrashFilter.ALL)
    val filter: StateFlow<TrashFilter> = _filter

    val uiState: StateFlow<TrashUiState> = combine(
        getTrashItemsUseCase(),
        _filter
    ) { allItems, currentFilter ->
        val filtered = when (currentFilter) {
            TrashFilter.ALL -> allItems
            TrashFilter.NOTES -> allItems.filterIsInstance<TrashItem.NoteTrash>()
            TrashFilter.TASKS -> allItems.filterIsInstance<TrashItem.TaskTrash>()
        }
        TrashUiState(
            items = filtered,
            filter = currentFilter,
            totalCount = allItems.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrashUiState(isLoading = true)
    )

    fun onFilterSelected(filter: TrashFilter) {
        _filter.value = filter
    }

    fun restoreItem(item: TrashItem) {
        viewModelScope.launch {
            restoreTrashItemUseCase(item)
        }
    }

    fun permanentlyDeleteItem(item: TrashItem) {
        viewModelScope.launch {
            permanentlyDeleteTrashItemUseCase(item)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            emptyAllTrashUseCase()
        }
    }
}
