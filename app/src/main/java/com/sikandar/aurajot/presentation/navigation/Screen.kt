package com.sikandar.aurajot.presentation.navigation

sealed class Screen(val route: String) {
    data object Notes : Screen("notes")
    data object NoteEdit : Screen("note_edit/{noteId}") {
        fun createRoute(noteId: Long = -1L): String = "note_edit/$noteId"
    }
    data object Tasks : Screen("tasks")
    data object Trash : Screen("trash")
}
