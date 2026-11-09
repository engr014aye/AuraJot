package com.sikandar.aurajot

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sikandar.aurajot.data.local.AuraJotDatabase
import com.sikandar.aurajot.data.local.entity.NoteEntity
import com.sikandar.aurajot.data.local.entity.TaskEntity
import com.sikandar.aurajot.data.repository.NoteRepositoryImpl
import com.sikandar.aurajot.data.repository.TaskRepositoryImpl
import com.sikandar.aurajot.domain.model.ChecklistItem
import com.sikandar.aurajot.domain.model.PriorityLevel
import com.sikandar.aurajot.domain.repository.NoteRepository
import com.sikandar.aurajot.domain.repository.TaskRepository
import com.sikandar.aurajot.domain.usecase.EmptyAllTrashUseCase
import com.sikandar.aurajot.domain.usecase.GetActiveNotesUseCase
import com.sikandar.aurajot.domain.usecase.GetActiveTasksUseCase
import com.sikandar.aurajot.domain.usecase.GetNoteByIdUseCase
import com.sikandar.aurajot.domain.usecase.GetTaskByIdUseCase
import com.sikandar.aurajot.domain.usecase.GetTrashItemsUseCase
import com.sikandar.aurajot.domain.usecase.PermanentlyDeleteNoteUseCase
import com.sikandar.aurajot.domain.usecase.PermanentlyDeleteTaskUseCase
import com.sikandar.aurajot.domain.usecase.PermanentlyDeleteTrashItemUseCase
import com.sikandar.aurajot.domain.usecase.RestoreNoteUseCase
import com.sikandar.aurajot.domain.usecase.RestoreTaskUseCase
import com.sikandar.aurajot.domain.usecase.RestoreTrashItemUseCase
import com.sikandar.aurajot.domain.usecase.SaveNoteUseCase
import com.sikandar.aurajot.domain.usecase.SaveTaskUseCase
import com.sikandar.aurajot.domain.usecase.SoftDeleteNoteUseCase
import com.sikandar.aurajot.domain.usecase.SoftDeleteTaskUseCase
import com.sikandar.aurajot.domain.usecase.TogglePinNoteUseCase
import com.sikandar.aurajot.domain.usecase.ToggleTaskCompletionUseCase
import com.sikandar.aurajot.presentation.screens.notes.NotesViewModel
import com.sikandar.aurajot.presentation.screens.tasks.TasksViewModel
import com.sikandar.aurajot.presentation.screens.trash.TrashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AuraJotApplication : Application() {

    lateinit var database: AuraJotDatabase
        private set

    lateinit var noteRepository: NoteRepository
        private set

    lateinit var taskRepository: TaskRepository
        private set

    // Use cases
    lateinit var getActiveNotesUseCase: GetActiveNotesUseCase
    lateinit var getNoteByIdUseCase: GetNoteByIdUseCase
    lateinit var saveNoteUseCase: SaveNoteUseCase
    lateinit var softDeleteNoteUseCase: SoftDeleteNoteUseCase
    lateinit var restoreNoteUseCase: RestoreNoteUseCase
    lateinit var permanentlyDeleteNoteUseCase: PermanentlyDeleteNoteUseCase
    lateinit var togglePinNoteUseCase: TogglePinNoteUseCase

    lateinit var getActiveTasksUseCase: GetActiveTasksUseCase
    lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    lateinit var saveTaskUseCase: SaveTaskUseCase
    lateinit var toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase
    lateinit var softDeleteTaskUseCase: SoftDeleteTaskUseCase
    lateinit var restoreTaskUseCase: RestoreTaskUseCase
    lateinit var permanentlyDeleteTaskUseCase: PermanentlyDeleteTaskUseCase

    lateinit var getTrashItemsUseCase: GetTrashItemsUseCase
    lateinit var restoreTrashItemUseCase: RestoreTrashItemUseCase
    lateinit var permanentlyDeleteTrashItemUseCase: PermanentlyDeleteTrashItemUseCase
    lateinit var emptyAllTrashUseCase: EmptyAllTrashUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AuraJotDatabase.getInstance(this)
        noteRepository = NoteRepositoryImpl(database.noteDao)
        taskRepository = TaskRepositoryImpl(database.taskDao)

        // Initialize Note UseCases
        getActiveNotesUseCase = GetActiveNotesUseCase(noteRepository)
        getNoteByIdUseCase = GetNoteByIdUseCase(noteRepository)
        saveNoteUseCase = SaveNoteUseCase(noteRepository)
        softDeleteNoteUseCase = SoftDeleteNoteUseCase(noteRepository)
        restoreNoteUseCase = RestoreNoteUseCase(noteRepository)
        permanentlyDeleteNoteUseCase = PermanentlyDeleteNoteUseCase(noteRepository)
        togglePinNoteUseCase = TogglePinNoteUseCase(noteRepository)

        // Initialize Task UseCases
        getActiveTasksUseCase = GetActiveTasksUseCase(taskRepository)
        getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)
        saveTaskUseCase = SaveTaskUseCase(taskRepository)
        toggleTaskCompletionUseCase = ToggleTaskCompletionUseCase(taskRepository)
        softDeleteTaskUseCase = SoftDeleteTaskUseCase(taskRepository)
        restoreTaskUseCase = RestoreTaskUseCase(taskRepository)
        permanentlyDeleteTaskUseCase = PermanentlyDeleteTaskUseCase(taskRepository)

        // Initialize Trash UseCases
        getTrashItemsUseCase = GetTrashItemsUseCase(noteRepository, taskRepository)
        restoreTrashItemUseCase = RestoreTrashItemUseCase(noteRepository, taskRepository)
        permanentlyDeleteTrashItemUseCase = PermanentlyDeleteTrashItemUseCase(noteRepository, taskRepository)
        emptyAllTrashUseCase = EmptyAllTrashUseCase(noteRepository, taskRepository)

        // Seed initial sample data if first run
        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        val prefs = getSharedPreferences("aurajot_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        if (isFirstLaunch) {
            applicationScope.launch {
                val currentTime = System.currentTimeMillis()

                // Welcome Note
                database.noteDao.insertOrUpdateNote(
                    NoteEntity(
                        title = "Welcome to AuraJot • Studio Clean",
                        content = "AuraJot is crafted with an iOS-inspired Studio Clean aesthetic.\n\n" +
                                "✨ Bento Glass Containers with hairline borders\n" +
                                "✨ Cupertino spring physics for fluid animations\n" +
                                "✨ Rich Markdown & text formatting toolbar\n" +
                                "✨ 100% Offline-first local Room storage with zero cloud dependencies\n" +
                                "✨ Non-destructive Soft Trash Bin for safe 1-tap recovery",
                        category = "Studio",
                        colorHex = "#1C202C",
                        isPinned = true,
                        isDeleted = false,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        deletedAt = null
                    )
                )

                // Ideas Note
                database.noteDao.insertOrUpdateNote(
                    NoteEntity(
                        title = "Editorial Design Principles",
                        content = "1. Generous negative space and breathing room\n" +
                                "2. Strict typographic hierarchy\n" +
                                "3. Translucent dark obsidian backdrops with subtle 15% white specular borders\n" +
                                "4. Luminous warm amber highlights for interactive elements",
                        category = "Ideas",
                        colorHex = "#182030",
                        isPinned = false,
                        isDeleted = false,
                        createdAt = currentTime - 3600000,
                        updatedAt = currentTime - 3600000,
                        deletedAt = null
                    )
                )

                // Starter Tasks
                database.taskDao.insertOrUpdateTask(
                    TaskEntity(
                        title = "Experience Cupertino bouncy checkbox",
                        notes = "Tap the circular checkbox to test the spring physics and progress bar.",
                        isCompleted = true,
                        priority = "HIGH",
                        dueDateMillis = currentTime + 86400000,
                        category = "Studio",
                        checklistRaw = "[]",
                        isDeleted = false,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        deletedAt = null
                    )
                )

                database.taskDao.insertOrUpdateTask(
                    TaskEntity(
                        title = "Explore rich text formatting & color tints",
                        notes = "Open a note to use bold, italic, headings, code, and color palettes.",
                        isCompleted = false,
                        priority = "MEDIUM",
                        dueDateMillis = currentTime + 172800000,
                        category = "Work",
                        checklistRaw = "[{\"id\":\"1\",\"text\":\"Try bold & italics\",\"isDone\":false},{\"id\":\"2\",\"text\":\"Switch note tint to Emerald or Midnight\",\"isDone\":false}]",
                        isDeleted = false,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        deletedAt = null
                    )
                )

                database.taskDao.insertOrUpdateTask(
                    TaskEntity(
                        title = "Test soft-deletion recovery in Trash Bin",
                        notes = "Items can be restored with 1-tap or permanently emptied.",
                        isCompleted = false,
                        priority = "LOW",
                        dueDateMillis = null,
                        category = "Personal",
                        checklistRaw = "[]",
                        isDeleted = false,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        deletedAt = null
                    )
                )

                prefs.edit().putBoolean("is_first_launch", false).apply()
            }
        }
    }

    val viewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(NotesViewModel::class.java) -> {
                    NotesViewModel(
                        getActiveNotesUseCase,
                        getNoteByIdUseCase,
                        saveNoteUseCase,
                        softDeleteNoteUseCase,
                        restoreNoteUseCase,
                        togglePinNoteUseCase
                    ) as T
                }
                modelClass.isAssignableFrom(TasksViewModel::class.java) -> {
                    TasksViewModel(
                        getActiveTasksUseCase,
                        getTaskByIdUseCase,
                        saveTaskUseCase,
                        toggleTaskCompletionUseCase,
                        softDeleteTaskUseCase,
                        restoreTaskUseCase
                    ) as T
                }
                modelClass.isAssignableFrom(TrashViewModel::class.java) -> {
                    TrashViewModel(
                        getTrashItemsUseCase,
                        restoreTrashItemUseCase,
                        permanentlyDeleteTrashItemUseCase,
                        emptyAllTrashUseCase
                    ) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
