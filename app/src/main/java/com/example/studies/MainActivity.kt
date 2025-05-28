package com.example.studies

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studies.view.screens.HomeScreen
import com.example.studies.view.screens.DisciplinesScreen
import com.example.studies.view.screens.WelcomeScreen
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.screens.SettingsScreen
import com.example.studies.view.screens.AddTaskScreen
import com.example.studies.viewmodel.TaskViewModel
import com.example.studies.view.screens.AddDisciplineScreen
import com.example.studies.view.screens.TasksScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudiesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    StudiesApp()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudiesApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val context = navController.context
    val sharedPref = context.getSharedPreferences("studies_prefs", Context.MODE_PRIVATE)
    val userNameSaved = sharedPref.contains("user_name")

    val startDestination = if (userNameSaved) "home" else "welcome"

    val taskViewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("disciplines") {
            DisciplinesScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("addTask") {
            AddTaskScreen(navController = navController, viewModel = taskViewModel)
        }
        composable("add_discipline") {
            AddDisciplineScreen(navController = navController)
        }
        composable("tasks"){
            TasksScreen(navController = navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StudiesTheme {
        StudiesApp()
    }
}