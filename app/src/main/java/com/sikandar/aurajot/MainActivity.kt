package com.sikandar.aurajot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.sikandar.aurajot.presentation.navigation.AuraJotNavHost
import com.sikandar.aurajot.presentation.screens.notes.NotesViewModel
import com.sikandar.aurajot.presentation.screens.tasks.TasksViewModel
import com.sikandar.aurajot.presentation.screens.trash.TrashViewModel
import com.sikandar.aurajot.presentation.theme.AuraJotTheme

class MainActivity : ComponentActivity() {

    private val app by lazy { application as AuraJotApplication }

    private val notesViewModel: NotesViewModel by viewModels { app.viewModelFactory }
    private val tasksViewModel: TasksViewModel by viewModels { app.viewModelFactory }
    private val trashViewModel: TrashViewModel by viewModels { app.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AuraJotTheme {
                val navController = rememberNavController()
                AuraJotNavHost(
                    navController = navController,
                    notesViewModel = notesViewModel,
                    tasksViewModel = tasksViewModel,
                    trashViewModel = trashViewModel
                )
            }
        }
    }
}
