package com.example.studies.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disciplines")
data class DisciplineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val location: String?,
    val professor: String?,
    val imageUri: String? = null
)