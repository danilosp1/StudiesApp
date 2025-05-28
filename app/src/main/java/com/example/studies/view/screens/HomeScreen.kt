package com.example.studies.view.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.view.components.Footer
import com.example.studies.ui.theme.StudiesTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.studies.R
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studies.StudiesApplication
import com.example.studies.data.dao.DisciplineWithSchedules
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.viewmodel.DisciplineViewModel
import com.example.studies.viewmodel.DisciplineViewModelFactory
import java.time.DayOfWeek

data class Subject(val name: String, val time: String, val location: String)

val screenBackgroundColor = Color(0xFFEAEAEA)
val primaryTextColorHomeScreen = Color(0xFF0E0E0E)
val secondaryTextColorHomeScreen = Color(0xFF757575)
val highlightColor = Color(0xFFFFF59D)
val dividerColorHomeScreen = Color(0xFF0E0E0E)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: DisciplineViewModel = viewModel(
        factory = DisciplineViewModelFactory((LocalContext.current.applicationContext as StudiesApplication).repository)
    )
) {
    val context = LocalContext.current
    val userName = remember { mutableStateOf(loadUserName(context)) }
    val disciplinesWithSchedulesUiState by viewModel.disciplinesWithSchedulesUiState.collectAsState()

    val today = LocalDate.now()
    val currentDayOfWeekPortuguese = remember(today, context) {
        when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> context.getString(R.string.seg_full)
            DayOfWeek.TUESDAY -> context.getString(R.string.ter_full)
            DayOfWeek.WEDNESDAY -> context.getString(R.string.qua_full)
            DayOfWeek.THURSDAY -> context.getString(R.string.qui_full)
            DayOfWeek.FRIDAY -> context.getString(R.string.sex_full)
            DayOfWeek.SATURDAY -> context.getString(R.string.sab_full)
            DayOfWeek.SUNDAY -> context.getString(R.string.dom_full)
        }
    }

    val todaySubjects = remember(disciplinesWithSchedulesUiState.disciplines, currentDayOfWeekPortuguese, context) {
        disciplinesWithSchedulesUiState.disciplines.flatMap { disciplineWithSchedules ->
            disciplineWithSchedules.schedules
                .filter { schedule -> schedule.dayOfWeek.equals(currentDayOfWeekPortuguese, ignoreCase = true) }
                .map { schedule ->
                    Subject(
                        name = disciplineWithSchedules.discipline.name,
                        time = "${schedule.startTime} - ${schedule.endTime}",
                        location = disciplineWithSchedules.discipline.location ?: context.getString(R.string.local_nao_definido)
                    )
                }
        }.sortedBy { it.time.substringBefore(" - ") }
    }

    val currentDateFormatted = today.format(DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR")))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = stringResource(id = R.string.Ola_Inicio, userName.value),
                fontSize = 28.sp,
                color = primaryTextColorHomeScreen,
            )

            Spacer(modifier = Modifier.height(7.dp))

            HorizontalDivider(
                color = dividerColorHomeScreen,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 45.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.AulasDoDia),
                    fontSize = 33.sp,
                    color = primaryTextColorHomeScreen
                )
                Text(
                    text = currentDateFormatted,
                    fontSize = 25.sp,
                    color = primaryTextColorHomeScreen
                )
            }

            Spacer(modifier = Modifier.padding(bottom = 30.dp))

            if (todaySubjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.sem_aulas_hoje),
                        fontSize = 18.sp,
                        color = secondaryTextColorHomeScreen
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    items(todaySubjects) { subject ->
                        SubjectCard(subject = subject)
                    }
                }
            }
        }
        Footer(navController = navController, currentRoute = "home")
    }
}

@Composable
fun SubjectCard(subject: Subject) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier
                .width(180.dp)
                .height(40.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val path = Path().apply {
                    moveTo(canvasWidth * 0.05f, canvasHeight * 0.5f)
                    lineTo(canvasWidth * 0.25f, canvasHeight * 0.1f)
                    lineTo(canvasWidth * 0.45f, canvasHeight * 0.5f)
                    lineTo(canvasWidth * 0.25f, canvasHeight * 0.9f)
                    close()
                    moveTo(canvasWidth * 0.95f, canvasHeight * 0.5f)
                    lineTo(canvasWidth * 0.75f, canvasHeight * 0.1f)
                    lineTo(canvasWidth * 0.55f, canvasHeight * 0.5f)
                    lineTo(canvasWidth * 0.75f, canvasHeight * 0.9f)
                    close()
                }
                drawPath(path, color = highlightColor)
            }
            Text(
                text = subject.time,
                fontSize = 25.sp,
                color = primaryTextColorHomeScreen
            )
        }

        HorizontalDivider(
            color = dividerColorHomeScreen,
            thickness = 1.dp,
            modifier = Modifier
                .width(160.dp)
                .padding(vertical = 3.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = stringResource(id = R.string.subject_icon_desc),
                modifier = Modifier.size(36.dp),
                tint = primaryTextColorHomeScreen
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = subject.name,
                    fontSize = 22.sp,
                    color = primaryTextColorHomeScreen
                )
                Text(
                    text = subject.location,
                    fontSize = 16.sp,
                    color = secondaryTextColorHomeScreen,
                )
            }
        }
    }
}

fun loadUserName(context: Context): String {
    val sharedPref = context.getSharedPreferences("studies_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("user_name", "User") ?: "User"
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    StudiesTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}