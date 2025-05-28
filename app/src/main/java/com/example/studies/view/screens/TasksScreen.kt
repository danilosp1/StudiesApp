package com.example.studies.view.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.R
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.components.Footer

data class TaskItemData(val id: String, val title: String, val dateTime: String, var isChecked: Boolean)

@Composable
fun TasksScreen(navController: NavController) {
    val tasks = remember {
        mutableStateListOf(
            TaskItemData("1", "Tarefa 1", "26/07 - 23:59", false),
            TaskItemData("2", "Tarefa 2", "27/07 - 18:00", false),
            TaskItemData("3", "Tarefa 3", "28/07 - 10:00", true),
            TaskItemData("4", "Tarefa 4", "29/07 - 12:30", false)
        )
    }

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
                    itemsIndexed(tasks) { index, task ->
                        TaskItemCard(
                            task = task,
                            onCheckedChange = { isChecked ->
                                tasks[index] = task.copy(isChecked = isChecked)
                            }
                        )
                    }
                }
            }
        }
        Footer(navController = navController, currentRoute = "tasks")
    }
}

@Composable
fun TaskItemCard(task: TaskItemData, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color(0xFF424242), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = task.dateTime,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.title,
                    fontSize = 20.sp,
                    color = Color(0xFF0E0E0E)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Checkbox(
            checked = task.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF0E0E0E),
                uncheckedColor = Color(0xFF0E0E0E),
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