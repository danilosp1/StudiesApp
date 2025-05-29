package com.example.studies.view.screens

import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studies.MainActivity
import com.example.studies.R
import com.example.studies.StudiesApplication
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.ui.theme.StudiesTheme
import com.example.studies.view.components.Footer
import com.example.studies.viewmodel.DisciplineViewModel
import com.example.studies.viewmodel.DisciplineViewModelFactory
import java.util.Locale

data class DaySchedule(
    val dayShort: String,
    val dayFull: String,
    var isSelected: Boolean,
    var startTime: String,
    var endTime: String
)

@Composable
fun AddDisciplineScreen(
    navController: NavController,
    viewModel: DisciplineViewModel = viewModel(
        factory = DisciplineViewModelFactory((LocalContext.current.applicationContext as StudiesApplication).repository)
    )
) {
    var disciplineName by remember { mutableStateOf("") }
    var disciplineLocation by remember { mutableStateOf("") }
    var disciplineProfessor by remember { mutableStateOf("") }

    val context = LocalContext.current

    val segShortText = stringResource(id = R.string.seg_short)
    val terShortText = stringResource(id = R.string.ter_short)
    val quaShortText = stringResource(id = R.string.qua_short)
    val quiShortText = stringResource(id = R.string.qui_short)
    val sexShortText = stringResource(id = R.string.sex_short)

    val segFullText = stringResource(id = R.string.seg_full)
    val terFullText = stringResource(id = R.string.ter_full)
    val quaFullText = stringResource(id = R.string.qua_full)
    val quiFullText = stringResource(id = R.string.qui_full)
    val sexFullText = stringResource(id = R.string.sex_full)

    val initialStartTime = "08:00"
    val initialEndTime = "12:00"

    val weekDaysAndSchedulesForUI = remember {
        mutableStateListOf(
            DaySchedule(segShortText, segFullText, false, initialStartTime, initialEndTime),
            DaySchedule(terShortText, terFullText, false, initialStartTime, initialEndTime),
            DaySchedule(quaShortText, quaFullText, false, initialStartTime, initialEndTime),
            DaySchedule(quiShortText, quiFullText, false, initialStartTime, initialEndTime),
            DaySchedule(sexShortText, sexFullText, false, initialStartTime, initialEndTime)
        )
    }

    fun showTimePicker(
        initialHour: Int,
        initialMinute: Int,
        is24HourView: Boolean = true,
        onTimeSelected: (hour: Int, minute: Int) -> Unit
    ) {
        TimePickerDialog(
            context,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                onTimeSelected(selectedHour, selectedMinute)
            },
            initialHour,
            initialMinute,
            is24HourView
        ).show()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(45.dp))
                Text(
                    text = stringResource(id = R.string.nova_disciplina_title),
                    fontSize = 30.sp,
                    color = primaryTextColor
                )
                Spacer(modifier = Modifier.height(7.dp))
                HorizontalDivider(
                    color = primaryTextColor,
                    thickness = 1.dp,
                )
                Spacer(modifier = Modifier.height(30.dp))
            }

            item {
                DisciplineInputTextField(
                    value = disciplineName,
                    onValueChange = { disciplineName = it },
                    label = stringResource(R.string.nome_label)
                )
                Spacer(modifier = Modifier.height(20.dp))
                DisciplineInputTextField(
                    value = disciplineLocation,
                    onValueChange = { disciplineLocation = it },
                    label = stringResource(R.string.local_label)
                )
                Spacer(modifier = Modifier.height(20.dp))
                DisciplineInputTextField(
                    value = disciplineProfessor,
                    onValueChange = { disciplineProfessor = it },
                    label = stringResource(R.string.professor_label)
                )
                Spacer(modifier = Modifier.height(20.dp))

            }

            item {
                Text(
                    text = stringResource(id = R.string.dias_da_semana_label),
                    fontSize = 22.sp,
                    color = primaryTextColor,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    weekDaysAndSchedulesForUI.forEachIndexed { index, dayInfo ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dayInfo.dayShort, fontSize = 16.sp, color = primaryTextColor)
                                Checkbox(
                                    checked = dayInfo.isSelected,
                                    onCheckedChange = { checked ->
                                        weekDaysAndSchedulesForUI[index] =
                                            dayInfo.copy(isSelected = checked)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = primaryTextColor,
                                        uncheckedColor = primaryTextColor,
                                        checkmarkColor = Color(0xFFF0F0F0)
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }

            item {
                Text(
                    text = stringResource(id = R.string.horarios_label),
                    fontSize = 22.sp,
                    color = primaryTextColor,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(weekDaysAndSchedulesForUI.size) { index ->
                val dayInfo = weekDaysAndSchedulesForUI[index]
                if (dayInfo.isSelected) {
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Text(
                            dayInfo.dayFull,
                            fontSize = 18.sp,
                            color = primaryTextColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimePickerField(
                                label = stringResource(R.string.inicio_label),
                                time = dayInfo.startTime,
                                onTimeClick = {
                                    val (currentHour, currentMinute) = dayInfo.startTime.split(":").map { it.toInt() }
                                    showTimePicker(currentHour, currentMinute) { hour, minute ->
                                        val newTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                        weekDaysAndSchedulesForUI[index] = dayInfo.copy(startTime = newTime)
                                    }
                                }
                            )
                            TimePickerField(
                                label = stringResource(R.string.termino_label),
                                time = dayInfo.endTime,
                                onTimeClick = {
                                    val (currentHour, currentMinute) = dayInfo.endTime.split(":").map { it.toInt() }
                                    showTimePicker(currentHour, currentMinute) { hour, minute ->
                                        val newTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                        weekDaysAndSchedulesForUI[index] = dayInfo.copy(endTime = newTime)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        if (disciplineName.isNotBlank()) {
                            val schedulesToSave = weekDaysAndSchedulesForUI
                                .filter { it.isSelected }
                                .map { uiSchedule ->
                                    SubjectScheduleEntity(
                                        disciplineId = 0,
                                        dayOfWeek = uiSchedule.dayFull,
                                        startTime = uiSchedule.startTime,
                                        endTime = uiSchedule.endTime
                                    )
                                }

                            viewModel.addDiscipline(
                                name = disciplineName,
                                location = disciplineLocation.takeIf { it.isNotBlank() },
                                professor = disciplineProfessor.takeIf { it.isNotBlank() },
                                schedules = schedulesToSave
                            )
                            Log.d("AddDisciplineScreen", "Discipline: $disciplineName, Schedules: $schedulesToSave")
                            navController.popBackStack()
                        } else {
                            Log.w("AddDisciplineScreen", "Discipline name is blank")
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, primaryTextColor),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    enabled = disciplineName.isNotBlank()
                ) {
                    Text(
                        text = stringResource(R.string.adicionar_button),
                        color = primaryTextColor,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
        Footer(navController = navController, currentRoute = "add_discipline")
    }
}

@Composable
fun DisciplineInputTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF6B6969), fontSize = 16.sp) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFF0E0E0E),
            unfocusedIndicatorColor = Color(0xFF0E0E0E),
            cursorColor = Color(0xFF0E0E0E),
            focusedTextColor = Color(0xFF0E0E0E),
            unfocusedTextColor = Color(0xFF0E0E0E),
            focusedLabelColor = Color(0xFF0E0E0E),
            unfocusedLabelColor = Color(0xFF6B6969)
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
    )
}

@Composable
fun TimePickerField(
    label: String,
    time: String,
    onTimeClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF6B6969),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedButton(
            onClick = onTimeClick,
            modifier = Modifier.width(150.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFF424242)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(time, fontSize = 18.sp, color = Color(0xFF0E0E0E))
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = stringResource(id = R.string.select_time_action),
                    tint = Color(0xFF0E0E0E)
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun PreviewAddDisciplineScreen() {
    StudiesTheme {
        val navController = rememberNavController()
        AddDisciplineScreen(navController = navController)
    }
}