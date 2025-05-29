package com.example.studies

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.studies.data.AppDatabase
import com.example.studies.data.repository.AppRepository
import com.example.studies.view.screens.HomeScreen
import com.example.studies.view.screens.DisciplinesScreen
import com.example.studies.view.screens.WelcomeScreen
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.screens.SettingsScreen
import com.example.studies.view.screens.AddTaskScreen
import com.example.studies.viewmodel.TaskViewModel
import com.example.studies.view.screens.AddDisciplineScreen
import com.example.studies.view.screens.DisciplineDetailScreen
import com.example.studies.view.screens.RegisterScreen // Importar RegisterScreen
import com.example.studies.view.screens.TaskDetailScreen
import com.example.studies.view.screens.TasksScreen
import com.example.studies.viewmodel.DisciplineViewModelFactory
import com.example.studies.viewmodel.TaskViewModelFactory
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

private fun loadLanguagePreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    val lang = sharedPreferences.getString("AppLanguage", "pt") ?: "pt"
    return lang
}

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val preferredLanguage = loadLanguagePreference(newBase)

        val appLocaleListCompat = LocaleListCompat.forLanguageTags(preferredLanguage)

        AppCompatDelegate.setApplicationLocales(appLocaleListCompat)

        val configuration = Configuration(newBase.resources.configuration)

        val locales = mutableListOf<Locale>()
        for (i in 0 until appLocaleListCompat.size()) {
            appLocaleListCompat[i]?.let { locales.add(it) }
        }

        if (locales.isNotEmpty()) {
            val androidLocaleList = LocaleList(*locales.toTypedArray())
            configuration.setLocales(androidLocaleList)
        }

        val updatedContext = newBase.createConfigurationContext(configuration)

        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudiesTheme {
                val composeContext = LocalContext.current
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    StudiesApp()
                }
            }
        }
    }
}

@Composable
fun StudiesApp() {
    val navController = rememberNavController()

    val application = LocalContext.current.applicationContext as StudiesApplication
    val repository = application.repository

    val sharedPref = remember(application) {
        application.getSharedPreferences("studies_prefs", Context.MODE_PRIVATE)
    }
    val userNameSaved = remember(sharedPref) {
        sharedPref.contains("user_name")
    }

    val startDestination = if (userNameSaved) "home" else "welcome"

    val taskViewModelFactory = remember(repository) { TaskViewModelFactory(repository) }
    val disciplineViewModelFactory = remember(repository) { DisciplineViewModelFactory(repository) }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("disciplines") {
            DisciplinesScreen(
                navController = navController,
                viewModel = viewModel(factory = disciplineViewModelFactory)
            )
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("addTask") {
            AddTaskScreen(
                navController = navController,
                viewModel = viewModel(factory = taskViewModelFactory)
            )
        }
        composable("add_discipline") {
            AddDisciplineScreen(
                navController = navController,
                viewModel = viewModel(factory = disciplineViewModelFactory)
            )
        }
        composable("tasks"){
            TasksScreen(
                navController = navController,
                viewModel = viewModel(factory = taskViewModelFactory)
            )
        }
        composable(
            route = "taskDetail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            if (taskId != null && taskId != -1L) {
                TaskDetailScreen(
                    navController = navController,
                    taskId = taskId,
                    viewModel = viewModel(factory = taskViewModelFactory)
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        composable(
            route = "disciplineDetail/{disciplineId}",
            arguments = listOf(navArgument("disciplineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val disciplineId = backStackEntry.arguments?.getLong("disciplineId")
            if (disciplineId != null && disciplineId != -1L) {
                DisciplineDetailScreen(
                    navController = navController,
                    disciplineId = disciplineId,
                    viewModel = viewModel(factory = disciplineViewModelFactory)
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StudiesTheme {
        Text(stringResource(id = R.string.app_preview_no_vm))
    }
}