package com.sikandar.aurajot.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sikandar.aurajot.domain.model.Note
import com.sikandar.aurajot.domain.usecase.GetActiveNotesUseCase
import com.sikandar.aurajot.domain.usecase.GetNoteByIdUseCase
import com.sikandar.aurajot.domain.usecase.RestoreNoteUseCase
import com.sikandar.aurajot.domain.usecase.SaveNoteUseCase
import com.sikandar.aurajot.domain.usecase.SoftDeleteNoteUseCase
import com.sikandar.aurajot.domain.usecase.TogglePinNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false
)

class NotesViewModel(
    private val getActiveNotesUseCase: GetActiveNotesUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val softDeleteNoteUseCase: SoftDeleteNoteUseCase,
    private val restoreNoteUseCase: RestoreNoteUseCase,
    private val togglePinNoteUseCase: TogglePinNoteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val categories = listOf("All", "Personal", "Work", "Ideas", "Studio", "Journal")

    val uiState: StateFlow<NotesUiState> = combine(
        getActiveNotesUseCase(),
        _searchQuery,
        _selectedCategory
    ) { allNotes, query, category ->
        val filtered = allNotes.filter { note ->
            val matchesCategory = if (category == "All") true else note.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                note.title.contains(query, ignoreCase = true) || note.content.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
        NotesUiState(
            notes = filtered,
            searchQuery = query,
            selectedCategory = category,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            togglePinNoteUseCase(note.id, !note.isPinned)
        }
    }

    fun softDelete(note: Note) {
        viewModelScope.launch {
            softDeleteNoteUseCase(note.id)
        }
    }

    fun restore(note: Note) {
        viewModelScope.launch {
            restoreNoteUseCase(note.id)
        }
    }

    suspend fun getNote(id: Long): Note? {
        return getNoteByIdUseCase(id)
    }

    suspend fun saveNote(note: Note): Long {
        return saveNoteUseCase(note)
    }
}
