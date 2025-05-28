package com.example.studies.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = DisciplineEntity::class,
        parentColumns = ["id"],
        childColumns = ["disciplineId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index(value = ["disciplineId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val disciplineId: Long?,
    val name: String,
    val description: String?,
    val dueDate: String?,
    val dueTime: String?,
    var isCompleted: Boolean = false
)