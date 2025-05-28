package com.example.studies.data.repository

import com.example.studies.data.dao.DisciplineDao
import com.example.studies.data.dao.TaskDao
// import com.example.studies.data.dao.MaterialLinkDao // Se for usar
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.data.model.TaskEntity
// import com.example.studies.data.model.MaterialLinkEntity // Se for usar
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val disciplineDao: DisciplineDao,
    private val taskDao: TaskDao
) {

    fun getAllDisciplinesWithSchedules(): Flow<List<com.example.studies.data.dao.DisciplineWithSchedules>> =
        disciplineDao.getAllDisciplinesWithSchedules()

    fun getDisciplineWithSchedulesById(id: Long): Flow<com.example.studies.data.dao.DisciplineWithSchedules?> =
        disciplineDao.getDisciplineWithSchedulesById(id)

    fun getAllDisciplines(): Flow<List<DisciplineEntity>> =
        disciplineDao.getAllDisciplines()

    suspend fun insertDisciplineWithSchedules(discipline: DisciplineEntity, schedules: List<SubjectScheduleEntity>) {
        val disciplineId = disciplineDao.insertDiscipline(discipline)
        val schedulesWithDisciplineId = schedules.map { it.copy(disciplineId = disciplineId) }
        disciplineDao.insertSchedules(schedulesWithDisciplineId)
    }

    suspend fun deleteDiscipline(discipline: DisciplineEntity) {
        disciplineDao.deleteDiscipline(discipline)
    }

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTaskById(id: Long): Flow<TaskEntity?> = taskDao.getTaskById(id)

    fun getTasksByDiscipline(disciplineId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByDiscipline(disciplineId)

    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }
}