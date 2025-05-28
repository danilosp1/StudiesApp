package com.example.studies.view.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

data class Subject(val name: String, val time: String, val location: String)

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userName = remember { mutableStateOf(loadUserName(context)) }


    val todaySubjects = listOf(
        Subject(stringResource(id = R.string.D1), "08:00 - 10:00", stringResource(id = R.string.HomeLocal)),
        Subject(stringResource(id = R.string.D2), "14:00 - 16:00", stringResource(id = R.string.HomeLocal)),
        Subject(stringResource(id = R.string.D4), "16:00 - 18:00", stringResource(id = R.string.HomeLocal))
    )

    val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR")))

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = stringResource(id = R.string.Ola_Inicio, userName.value),
                fontSize = 30.sp,
                color = Color(0xFF424242),
            )

            Spacer(modifier = Modifier.height(7.dp))

            HorizontalDivider(
                color = Color(0xFF0E0E0E),
                thickness = 2.dp,
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
                    color = Color(0xFF0E0E0E)
                )
                Text(
                    text = currentDate,
                    fontSize = 25.sp,
                    color = Color(0xFF0E0E0E)
                )
            }

            Spacer(modifier = Modifier.padding(bottom = 30.dp))

            LazyColumn {
                items(todaySubjects) { subject ->
                    SubjectCard(subject = subject)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        Footer(navController = navController, currentRoute = "home")
    }
}

@Composable
fun SubjectCard(subject: Subject) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = subject.time, fontSize = 25.sp, color = Color(0xFF0E0E0E), modifier = Modifier.align(Alignment.CenterHorizontally))
        HorizontalDivider(
            color = Color(0xFF0E0E0E),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 3.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = subject.name, fontSize = 22.sp, color = Color(0xFF0E0E0E), modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(text = subject.location,
            fontSize = 16.sp,
            color = Color(0xFF0E0E0E),
            modifier = Modifier.align(Alignment.CenterHorizontally))
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