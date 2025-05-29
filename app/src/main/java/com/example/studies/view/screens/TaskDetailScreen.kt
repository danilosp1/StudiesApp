package com.example.studies.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.R
import com.example.studies.StudiesApplication
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.components.Footer
import com.example.studies.viewmodel.TaskViewModel
import com.example.studies.viewmodel.TaskViewModelFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    taskId: Long,
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory((LocalContext.current.applicationContext as StudiesApplication).repository)
    )
) {
    LaunchedEffect(taskId) {
        if (taskId != -1L) {
            viewModel.loadTaskById(taskId)
        }
    }

    val uiState by viewModel.selectedTaskDetailUiState.collectAsState()
    val task = uiState.task
    val disciplineName = uiState.disciplineName
    val isLoading = uiState.isLoading
    val errorMessage = uiState.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.detalhes_tarefa_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.voltar)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Footer(navController = navController, currentRoute = null)
        },
        content = { paddingValues ->
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.erro_placeholder, errorMessage), color = MaterialTheme.colorScheme.error)
                    }
                }
                task == null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.tarefa_nao_encontrada))
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = task.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                        disciplineName?.let {
                            Text(
                                text = it,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        } ?: Text(
                            text = stringResource(id = R.string.tarefa_geral_label),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )


                        DetailSection(title = stringResource(id = R.string.descricao_section_title), content = task.description ?: stringResource(id = R.string.nenhuma_descricao_placeholder))
                        DetailSection(title = stringResource(id = R.string.prazo_entrega_section_title), content = formatTaskDeadline(task.dueDate, task.dueTime))
                        DetailSection(title = stringResource(id = R.string.status_section_title), content = if (task.isCompleted) stringResource(id = R.string.status_concluida) else stringResource(id = R.string.status_pendente),
                            contentColor = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                viewModel.updateTaskCompletion(task, !task.isCompleted)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(50.dp)
                        ) {
                            Text(
                                text = if (task.isCompleted) stringResource(id = R.string.marcar_pendente_button) else stringResource(id = R.string.concluir_tarefa_button),
                                fontSize = 18.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.deleteTask(task)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(50.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.deletar_tarefa_button),
                                color = MaterialTheme.colorScheme.onError,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun DetailSection(title: String, content: String, contentColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = content,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            color = contentColor
        )
    }
}

@Composable
private fun formatTaskDeadline(dueDateStr: String?, dueTimeStr: String?): String {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val displayTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale("pt", "BR"))

    val prazoNaoDefinido = stringResource(id = R.string.prazo_nao_definido)
    val deadlineTimeOnlyFormat = stringResource(id = R.string.deadline_time_only)


    val date = try { dueDateStr?.let { LocalDate.parse(it, dateFormatter) } } catch (e: DateTimeParseException) { null }
    val time = try { dueTimeStr?.let { LocalTime.parse(it, timeFormatter) } } catch (e: DateTimeParseException) { null }

    return when {
        date != null && time != null -> "${date.format(dateFormatter)} - ${time.format(displayTimeFormatter)}"
        date != null -> date.format(dateFormatter)
        time != null -> String.format(deadlineTimeOnlyFormat, time.format(displayTimeFormatter))
        else -> prazoNaoDefinido
    }
}


@Preview(showBackground = true, name = "TaskDetailScreen Preview")
@Composable
fun TaskDetailScreenPreview() {
    StudiesTheme {
        TaskDetailScreen(navController = rememberNavController(), taskId = -1L)
    }
}