package com.sikandar.aurajot.domain.usecase

import com.sikandar.aurajot.domain.model.Note
import com.sikandar.aurajot.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetActiveNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getActiveNotes()
}

class GetNoteByIdUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Note? = repository.getNoteById(id)
}

class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Long = repository.insertOrUpdateNote(note)
}

class SoftDeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long) = repository.softDeleteNote(id)
}

class RestoreNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long) = repository.restoreNote(id)
}

class PermanentlyDeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long) = repository.permanentlyDeleteNote(id)
}

class TogglePinNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long, isPinned: Boolean) = repository.togglePin(id, isPinned)
}
