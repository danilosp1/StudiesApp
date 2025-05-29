package com.example.studies.data.repository

import com.example.studies.data.dao.DisciplineDao
import com.example.studies.data.dao.DisciplineWithSchedules
import com.example.studies.data.dao.MaterialLinkDao
import com.example.studies.data.dao.TaskDao
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.MaterialLinkEntity
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.data.model.TaskEntity
import com.example.studies.data.remote.ApiService
import com.example.studies.data.remote.MotdResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppRepository(
    private val disciplineDao: DisciplineDao,
    private val taskDao: TaskDao,
    private val materialLinkDao: MaterialLinkDao,
    private val apiService: ApiService
) {

    fun getAllDisciplinesWithSchedules(): Flow<List<DisciplineWithSchedules>> =
        disciplineDao.getAllDisciplinesWithSchedules()

    fun getDisciplineWithSchedulesById(id: Long): Flow<DisciplineWithSchedules?> =
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

    fun getMaterialLinksByDiscipline(disciplineId: Long): Flow<List<MaterialLinkEntity>> =
        materialLinkDao.getMaterialLinksByDiscipline(disciplineId)

    suspend fun insertMaterialLink(materialLink: MaterialLinkEntity) {
        materialLinkDao.insertMaterialLink(materialLink)
    }

    suspend fun updateMaterialLink(materialLink: MaterialLinkEntity) {
        materialLinkDao.updateMaterialLink(materialLink)
    }

    suspend fun deleteMaterialLink(materialLink: MaterialLinkEntity) {
        materialLinkDao.deleteMaterialLink(materialLink)
    }

    suspend fun getMessageOfTheDay(): Flow<MotdResponse> = flow {
        try {
            val message = apiService.getMessageOfTheDay()
            emit(message)
        } catch (e: Exception) {
            throw e
        }
    }
}