package com.example.studies.data.dao

import androidx.room.*
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.SubjectScheduleEntity
import kotlinx.coroutines.flow.Flow

data class DisciplineWithSchedules(
    @Embedded val discipline: DisciplineEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "disciplineId"
    )
    val schedules: List<SubjectScheduleEntity>
)

@Dao
interface DisciplineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscipline(discipline: DisciplineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<SubjectScheduleEntity>)

    @Update
    suspend fun updateDiscipline(discipline: DisciplineEntity)

    @Delete
    suspend fun deleteDiscipline(discipline: DisciplineEntity)

    @Transaction
    @Query("SELECT * FROM disciplines WHERE id = :id")
    fun getDisciplineWithSchedulesById(id: Long): Flow<DisciplineWithSchedules?>

    @Transaction
    @Query("SELECT * FROM disciplines ORDER BY name ASC")
    fun getAllDisciplinesWithSchedules(): Flow<List<DisciplineWithSchedules>>

    @Query("SELECT * FROM disciplines ORDER BY name ASC")
    fun getAllDisciplines(): Flow<List<DisciplineEntity>>
}