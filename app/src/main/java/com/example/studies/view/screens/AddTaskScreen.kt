package com.example.studies.view.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studies.StudiesApplication
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.view.components.Footer
import com.example.studies.viewmodel.TaskViewModel
import com.example.studies.viewmodel.TaskViewModelFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale

val primaryTextColor = Color(0xFF0E0E0E)
val secondaryTextColor = Color(0xFF6B6969)
val borderColor = Color(0xFF424242)
val indicatorColor = Color(0xFF0E0E0E)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = secondaryTextColor, fontSize = 16.sp) },
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = indicatorColor,
            unfocusedIndicatorColor = indicatorColor,
            cursorColor = primaryTextColor,
            focusedTextColor = primaryTextColor,
            unfocusedTextColor = primaryTextColor,
            focusedLabelColor = primaryTextColor,
            unfocusedLabelColor = secondaryTextColor
        ),
        singleLine = singleLine,
        minLines = minLines,
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, color = primaryTextColor),
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        interactionSource = interactionSource
    )
}

@Composable
fun StyledTaskPickerField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = secondaryTextColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, borderColor),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (value.isNotBlank()) value else placeholder,
                    fontSize = 18.sp,
                    color = if (value.isNotBlank()) primaryTextColor else secondaryTextColor
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Select $label",
                    tint = primaryTextColor
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory((LocalContext.current.applicationContext as StudiesApplication).repository)
    )
) {
    var taskName by remember { mutableStateOf("") }
    var selectedDiscipline by remember { mutableStateOf<DisciplineEntity?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    var disciplineExpanded by remember { mutableStateOf(false) }
    val disciplineOptions by viewModel.disciplinesStateFlow.collectAsState()


    val context = LocalContext.current
    val currentCalendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
        }
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                selectedTime = LocalTime.of(hourOfDay, minute)
            },
            currentCalendar.get(Calendar.HOUR_OF_DAY),
            currentCalendar.get(Calendar.MINUTE),
            true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Nova Tarefa", color = primaryTextColor) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Footer(navController = navController, currentRoute = "addTask" )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StyledTaskTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = "Nome"
                )
                Spacer(modifier = Modifier.height(20.dp))

                Box {
                    val disciplineInteractionSource = remember { MutableInteractionSource() }
                    StyledTaskTextField(
                        value = selectedDiscipline?.name ?: "",
                        onValueChange = {  },
                        label = "Disciplina",
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Selecionar Disciplina",
                                Modifier.clickable { disciplineExpanded = !disciplineExpanded }
                            )
                        },
                        interactionSource = disciplineInteractionSource
                    )

                    LaunchedEffect(disciplineInteractionSource) {
                        disciplineInteractionSource.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Release) {
                                disciplineExpanded = !disciplineExpanded
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = disciplineExpanded,
                        onDismissRequest = { disciplineExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (disciplineOptions.isEmpty()){
                            DropdownMenuItem(
                                text = { Text("Nenhuma disciplina cadastrada", color = secondaryTextColor) },
                                onClick = { disciplineExpanded = false }
                            )
                        }
                        disciplineOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name, color = primaryTextColor) },
                                onClick = {
                                    selectedDiscipline = option
                                    disciplineExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Nenhuma (tarefa geral)", color = secondaryTextColor) },
                            onClick = {
                                selectedDiscipline = null
                                disciplineExpanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                StyledTaskTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descrição",
                    singleLine = false,
                    minLines = 4
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Prazo de entrega",
                    fontSize = 22.sp,
                    color = primaryTextColor,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StyledTaskPickerField(
                        label = "Data",
                        value = selectedDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale("pt", "BR"))) ?: "",
                        placeholder = "DD/MM/AAAA",
                        onClick = {
                            val cal = Calendar.getInstance()
                            selectedDate?.let {
                                cal.set(it.year, it.monthValue - 1, it.dayOfMonth)
                            }
                            datePickerDialog.updateDate(
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    StyledTaskPickerField(
                        label = "Horário",
                        value = selectedTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale("pt", "BR"))) ?: "",
                        placeholder = "HH:MM",
                        onClick = {
                            val cal = Calendar.getInstance()
                            selectedTime?.let {
                                cal.set(Calendar.HOUR_OF_DAY, it.hour)
                                cal.set(Calendar.MINUTE, it.minute)
                            }
                            timePickerDialog.updateTime(
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE)
                            )
                            timePickerDialog.show()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (taskName.isNotBlank()) {
                            viewModel.addTask(
                                name = taskName,
                                disciplineId = selectedDiscipline?.id,
                                description = description,
                                dueDate = selectedDate,
                                dueTime = selectedTime
                            )
                            Log.d("AddTaskScreen", "Task added: $taskName, Discipline: ${selectedDiscipline?.name}")
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, primaryTextColor),
                    enabled = taskName.isNotBlank()
                ) {
                    Text(
                        text = "Adicionar",
                        color = primaryTextColor,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    )
}