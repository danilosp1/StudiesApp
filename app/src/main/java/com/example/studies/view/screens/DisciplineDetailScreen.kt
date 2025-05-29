package com.example.studies.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.R
import com.example.studies.StudiesApplication
import com.example.studies.data.model.MaterialLinkEntity
import com.example.studies.data.model.TaskEntity
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.components.Footer
import com.example.studies.viewmodel.DisciplineViewModel
import com.example.studies.viewmodel.DisciplineViewModelFactory
import com.example.studies.viewmodel.SelectedDisciplineDetailState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

val dettaglioPrimaryTextColor = Color(0xFF0E0E0E)
val dettaglioSecondaryTextColor = Color(0xFF424242)
val dettaglioBorderColor = Color(0xFF424242)
val dettaglioErrorColor = Color(0xFFB00020)
val dettaglioBackgroundColor = Color(0xFFEAEAEA)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisciplineDetailScreen(
    navController: NavController,
    disciplineId: Long,
    viewModel: DisciplineViewModel = viewModel(
        factory = DisciplineViewModelFactory(
            (LocalContext.current.applicationContext as StudiesApplication).repository
        )
    )
) {
    LaunchedEffect(disciplineId) {
        if (disciplineId != -1L) {
            viewModel.loadDisciplineDetailsById(disciplineId)
        }
    }

    val selectedDisciplineState by viewModel.selectedDisciplineDetailState.collectAsState()
    val disciplineWithSchedules = selectedDisciplineState.disciplineWithSchedules
    val discipline = disciplineWithSchedules?.discipline
    val schedules = disciplineWithSchedules?.schedules ?: emptyList()
    val tasksForDiscipline = selectedDisciplineState.tasks
    val materialLinks = selectedDisciplineState.materialLinks
    val isLoading = selectedDisciplineState.isLoading
    val errorMessage = selectedDisciplineState.errorMessage

    val uriHandler = LocalUriHandler.current
    var showAddLinkDialog by remember { mutableStateOf(false) }
    var newLinkUrl by remember { mutableStateOf("") }
    var newLinkDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalhes_disciplina)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar),
                            tint = dettaglioPrimaryTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Footer(navController = navController, currentRoute = null)
        },
        containerColor = dettaglioBackgroundColor
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.erro_placeholder, errorMessage), color = dettaglioErrorColor)
                }
            }
            discipline == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.disciplina_nao_encontrada))
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(dettaglioSecondaryTextColor.copy(alpha = 0.1f))
                                .border(1.dp, dettaglioBorderColor, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.DateRange,
                                contentDescription = stringResource(R.string.icone_calendario_disciplina),
                                modifier = Modifier.size(60.dp),
                                tint = dettaglioPrimaryTextColor
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = discipline.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = dettaglioPrimaryTextColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = dettaglioPrimaryTextColor)

                        if (!discipline.professor.isNullOrBlank()) {
                            DetailItem(label = stringResource(R.string.professor_label_detail), value = discipline.professor)
                        }
                        if (!discipline.location.isNullOrBlank()) {
                            DetailItem(label = stringResource(R.string.local_label_detail), value = discipline.location)
                        }
                        if (schedules.isNotEmpty()) {
                            DetailItem(label = stringResource(R.string.horarios_label_detail), value = formatSchedules(schedules))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        SectionTitleWithAdd(
                            title = stringResource(R.string.materiais_label),
                            onAddClick = { showAddLinkDialog = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (materialLinks.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.sem_materiais_cadastrados),
                                color = dettaglioSecondaryTextColor,
                                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(materialLinks, key = { "material-${it.id}" }) { link ->
                            MaterialLinkItem(
                                link = link,
                                onDeleteClick = { viewModel.deleteMaterialLink(link) },
                                onLinkClick = {
                                    try {
                                        uriHandler.openUri(link.url)
                                    } catch (e: Exception) {

                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitleWithAdd(
                            title = stringResource(R.string.tarefas_label),
                            onAddClick = { navController.navigate("addTask?disciplineId=${discipline.id}") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (tasksForDiscipline.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.sem_tarefas_cadastradas_disciplina),
                                color = dettaglioSecondaryTextColor,
                                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(tasksForDiscipline, key = { "task-${it.id}" }) { task ->
                            TaskItemCardDisciplina(
                                task = task,
                                onCheckedChange = { isChecked -> task.isCompleted = !isChecked },
                                onClick = { navController.navigate("taskDetail/${task.id}") }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.deleteSelectedDiscipline {
                                    navController.popBackStack()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = dettaglioErrorColor),
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(50.dp),
                            // enabled = discipline != null
                        ) {
                            Text(
                                text = stringResource(R.string.deletar_disciplina_button),
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    if (showAddLinkDialog) {
        AlertDialog(
            onDismissRequest = { showAddLinkDialog = false },
            title = { Text(stringResource(R.string.adicionar_link_material)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newLinkUrl,
                        onValueChange = { newLinkUrl = it },
                        label = { Text(stringResource(R.string.url_link)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLinkDescription,
                        onValueChange = { newLinkDescription = it },
                        label = { Text(stringResource(R.string.descricao_link_opcional)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLinkUrl.isNotBlank()) {
                            viewModel.addMaterialLinkToSelectedDiscipline(newLinkUrl, newLinkDescription)
                            newLinkUrl = ""
                            newLinkDescription = ""
                            showAddLinkDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.adicionar_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLinkDialog = false }) {
                    Text(stringResource(R.string.cancelar_button))
                }
            }
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text(
                text = "$label ",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = dettaglioPrimaryTextColor
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = dettaglioSecondaryTextColor
            )
        }
    }
}

@Composable
private fun formatSchedules(schedules: List<com.example.studies.data.model.SubjectScheduleEntity>): String {
    if (schedules.isEmpty()) return stringResource(id = R.string.nenhum_horario_cadastrado)
    return schedules.joinToString(separator = "\n") {
        "${it.dayOfWeek}: ${it.startTime} - ${it.endTime}"
    }
}

@Composable
fun SectionTitleWithAdd(title: String, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = dettaglioPrimaryTextColor
        )
        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(dettaglioSecondaryTextColor.copy(alpha = 0.1f))
                .border(1.dp, dettaglioPrimaryTextColor, CircleShape)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.adicionar),
                tint = dettaglioPrimaryTextColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun MaterialLinkItem(
    link: MaterialLinkEntity,
    onDeleteClick: () -> Unit,
    onLinkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onLinkClick)
                .padding(end = 8.dp)
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = stringResource(R.string.icone_link),
                tint = dettaglioPrimaryTextColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = link.description?.takeIf { it.isNotBlank() } ?: link.url,
                fontSize = 16.sp,
                color = dettaglioPrimaryTextColor,
                textDecoration = TextDecoration.Underline,
                maxLines = 1
            )
        }
        IconButton(onClick = onDeleteClick, modifier = Modifier.size(22.dp)) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.remover_link),
                tint = dettaglioErrorColor.copy(alpha = 0.7f)
            )
        }
    }
}


@Composable
fun TaskItemCardDisciplina(
    task: TaskEntity,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val dateOnlyFormatter = remember { DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR")) }
    val timeOnlyFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale("pt", "BR")) }

    val formattedDateTime = remember(task.dueDate, task.dueTime) {
        try {
            val date = task.dueDate?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
            val time = task.dueTime?.let { LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm")) }

            when {
                date != null && time != null -> "${date.format(dateOnlyFormatter)} - ${time.format(timeOnlyFormatter)}"
                date != null -> date.format(dateOnlyFormatter)
                time != null -> time.format(timeOnlyFormatter)
                else -> ""
            }
        } catch (e: DateTimeParseException) {
            task.dueDate ?: ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, dettaglioBorderColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (formattedDateTime.isNotBlank()) {
                    Text(
                        text = formattedDateTime,
                        fontSize = 13.sp,
                        color = dettaglioSecondaryTextColor,
                        fontStyle = FontStyle.Italic
                    )
                }
                Text(
                    text = task.name,
                    fontSize = 20.sp,
                    color = dettaglioPrimaryTextColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEAEAEA)
@Composable
fun PreviewDisciplineDetailScreen() {
    StudiesTheme {
        DisciplineDetailScreen(navController = rememberNavController(), disciplineId = 1L)
    }
}