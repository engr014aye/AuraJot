package com.sikandar.aurajot.data.repository

import com.sikandar.aurajot.data.local.dao.NoteDao
import com.sikandar.aurajot.data.mapper.toDomain
import com.sikandar.aurajot.data.mapper.toEntity
import com.sikandar.aurajot.domain.model.Note
import com.sikandar.aurajot.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun getActiveNotes(): Flow<List<Note>> {
        return dao.getActiveNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDeletedNotes(): Flow<List<Note>> {
        return dao.getDeletedNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return dao.getNoteById(id)?.toDomain()
    }

    override suspend fun insertOrUpdateNote(note: Note): Long {
        return dao.insertOrUpdateNote(note.toEntity())
    }

    override suspend fun softDeleteNote(id: Long, deletedAt: Long) {
        dao.softDeleteNote(id, deletedAt)
    }

    override suspend fun restoreNote(id: Long) {
        dao.restoreNote(id)
    }

    override suspend fun permanentlyDeleteNote(id: Long) {
        dao.permanentlyDeleteNote(id)
    }

    override suspend fun emptyNoteTrash() {
        dao.emptyTrash()
    }

    override suspend fun togglePin(id: Long, isPinned: Boolean) {
        dao.togglePin(id, isPinned)
    }
}
