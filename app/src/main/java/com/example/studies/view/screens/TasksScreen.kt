package com.example.studies.view.screens
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.MainActivity
import com.example.studies.R
import com.example.studies.StudiesApplication
import com.example.studies.data.model.TaskEntity
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.components.Footer
import com.example.studies.viewmodel.TaskViewModel
import com.example.studies.viewmodel.TaskViewModelFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory((LocalContext.current.applicationContext as StudiesApplication).repository)
    )
) {
    val uiState by viewModel.taskUiState.collectAsState()
    val tasks = uiState.tasks


    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(45.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.tarefas_title),
                    fontSize = 30.sp,
                    color = Color(0xFF0E0E0E)
                )
                FloatingActionButton(
                    onClick = { navController.navigate("addTask") },
                    containerColor = Color(0xFF6B6969),
                    contentColor = Color.White,
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_tarefa_fab_desc)
                    )
                }
            }
            Spacer(modifier = Modifier.height(7.dp))
            HorizontalDivider(
                color = Color(0xFF0E0E0E),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(id = R.string.nenhuma_tarefa_adicionada), fontSize = 18.sp, color = secondaryTextColor)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFB0B0B0), RoundedCornerShape(20.dp))
                        .padding(vertical = 8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tasks, key = { it.id }) { taskEntity ->
                            TaskItemCard(
                                task = taskEntity,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateTaskCompletion(taskEntity, isChecked)
                                },
                                onClick = { navController.navigate("taskDetail/${taskEntity.id}") }
                            )
                        }
                    }
                }
            }
        }
        Footer(navController = navController, currentRoute = "tasks")
    }
}
@Composable
fun TaskItemCard(
    task: TaskEntity,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit = {}
) {
    val dateTimeFormater = remember { DateTimeFormatter.ofPattern("dd/MM - HH:mm", Locale("pt", "BR")) }
    val dateOnlyFormater = remember { DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR")) }
    val noDeadlineText = stringResource(id = R.string.sem_prazo)
    val formattedDateTime = remember(task.dueDate, task.dueTime) {
        try {
            val date = task.dueDate?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
            val time = task.dueTime?.let { LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm")) }

            when {
                date != null && time != null -> "${date.format(dateOnlyFormater)} - ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                date != null -> date.format(dateOnlyFormater)
                else -> noDeadlineText
            }
        } catch (e: Exception) {
            task.dueDate ?: noDeadlineText
        }
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color(0xFF424242), RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = formattedDateTime,
                    fontSize = 13.sp,
                    color = secondaryTextColor,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.name,
                    fontSize = 20.sp,
                    color = primaryTextColor
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = primaryTextColor,
                uncheckedColor = primaryTextColor,
                checkmarkColor = Color(0xFFF0F0F0)
            ),
            modifier = Modifier.size(24.dp)
        )
    }
}
@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun PreviewTasksScreen() {
    StudiesTheme {
        val navController = rememberNavController()
        TasksScreen(navController = navController)
    }
}