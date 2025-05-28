package com.example.studies.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subject_schedules",
    foreignKeys = [ForeignKey(
        entity = DisciplineEntity::class,
        parentColumns = ["id"],
        childColumns = ["disciplineId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["disciplineId"])]
)
data class SubjectScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val disciplineId: Long,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String
)