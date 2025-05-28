package com.example.studies.data

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var discipline: String,
    var description: String,
    var dueDate: LocalDate?,
    var dueTime: LocalTime?,
    var isCompleted: Boolean = false
)