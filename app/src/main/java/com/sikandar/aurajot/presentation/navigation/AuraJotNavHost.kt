package com.sikandar.aurajot.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.sikandar.aurajot.presentation.components.BottomNavTab
import com.sikandar.aurajot.presentation.components.GlassBottomNav
import com.sikandar.aurajot.presentation.screens.notes.NoteEditScreen
import com.sikandar.aurajot.presentation.screens.notes.NotesScreen
import com.sikandar.aurajot.presentation.screens.notes.NotesViewModel
import com.sikandar.aurajot.presentation.screens.tasks.TasksScreen
import com.sikandar.aurajot.presentation.screens.tasks.TasksViewModel
import com.sikandar.aurajot.presentation.screens.trash.TrashScreen
import com.sikandar.aurajot.presentation.screens.trash.TrashViewModel
import com.sikandar.aurajot.presentation.theme.ObsidianBlack

@Composable
fun AuraJotNavHost(
    navController: NavHostController,
    notesViewModel: NotesViewModel,
    tasksViewModel: TasksViewModel,
    trashViewModel: TrashViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Screen.Notes.route -> BottomNavTab.NOTES
        Screen.Tasks.route -> BottomNavTab.TASKS
        Screen.Trash.route -> BottomNavTab.TRASH
        else -> BottomNavTab.NOTES
    }

    val showBottomNav = currentRoute in listOf(
        Screen.Notes.route,
        Screen.Tasks.route,
        Screen.Trash.route
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Notes.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Notes.route) {
                NotesScreen(
                    viewModel = notesViewModel,
                    onNoteClick = { noteId ->
                        navController.navigate(Screen.NoteEdit.createRoute(noteId))
                    },
                    onCreateNote = {
                        navController.navigate(Screen.NoteEdit.createRoute(-1L))
                    }
                )
            }

            composable(
                route = Screen.NoteEdit.route,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                NoteEditScreen(
                    noteId = noteId,
                    viewModel = notesViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Tasks.route) {
                TasksScreen(
                    viewModel = tasksViewModel
                )
            }

            composable(Screen.Trash.route) {
                TrashScreen(
                    viewModel = trashViewModel
                )
            }
        }

        // Floating Glass Bottom Navigation Dock
        AnimatedVisibility(
            visible = showBottomNav,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            GlassBottomNav(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            popUpTo(Screen.Notes.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
