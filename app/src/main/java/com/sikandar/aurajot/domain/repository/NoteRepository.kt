package com.sikandar.aurajot.domain.repository

import com.sikandar.aurajot.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getActiveNotes(): Flow<List<Note>>
    fun getDeletedNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertOrUpdateNote(note: Note): Long
    suspend fun softDeleteNote(id: Long, deletedAt: Long = System.currentTimeMillis())
    suspend fun restoreNote(id: Long)
    suspend fun permanentlyDeleteNote(id: Long)
    suspend fun emptyNoteTrash()
    suspend fun togglePin(id: Long, isPinned: Boolean)
}
