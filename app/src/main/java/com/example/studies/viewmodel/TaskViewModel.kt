package com.example.studies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.TaskEntity
import com.example.studies.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TaskDetailUiState(
    val task: TaskEntity? = null,
    val disciplineName: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val disciplines: List<DisciplineEntity> = emptyList()
)

class TaskViewModel(private val repository: AppRepository) : ViewModel() {
    val taskUiState: StateFlow<TaskUiState> =
        repository.getAllTasks()
            .combine(repository.getAllDisciplines()) { tasks, disciplines ->
                TaskUiState(tasks = tasks, disciplines = disciplines)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = TaskUiState()
            )

    val disciplinesStateFlow: StateFlow<List<DisciplineEntity>> =
        repository.getAllDisciplines()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    private val _selectedTaskDetailUiState = MutableStateFlow(TaskDetailUiState())
    val selectedTaskDetailUiState: StateFlow<TaskDetailUiState> = _selectedTaskDetailUiState.asStateFlow()

    fun loadTaskById(taskId: Long) {
        viewModelScope.launch {
            _selectedTaskDetailUiState.value = TaskDetailUiState(isLoading = true)
            try {
                val taskFlow = repository.getTaskById(taskId).filterNotNull()
                val task = taskFlow.first()

                val disciplineName = task.disciplineId?.let { discId ->
                    repository.getAllDisciplines().first().find { it.id == discId }?.name
                }

                _selectedTaskDetailUiState.value = TaskDetailUiState(
                    task = task,
                    disciplineName = disciplineName,
                    isLoading = false
                )
            } catch (e: Exception) {
                _selectedTaskDetailUiState.value = TaskDetailUiState(
                    isLoading = false,
                    errorMessage = "Erro ao carregar tarefa: ${e.localizedMessage}"
                )
            }
        }
    }


    fun addTask(
        name: String,
        disciplineId: Long?,
        description: String?,
        dueDate: LocalDate?,
        dueTime: LocalTime?
    ) {
        viewModelScope.launch {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val task = TaskEntity(
                name = name,
                disciplineId = disciplineId,
                description = description,
                dueDate = dueDate?.format(dateFormatter),
                dueTime = dueTime?.format(timeFormatter),
                isCompleted = false
            )
            repository.insertTask(task)
        }
    }

    fun updateTaskCompletion(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = isCompleted))
            if (_selectedTaskDetailUiState.value.task?.id == task.id) {
                _selectedTaskDetailUiState.value = _selectedTaskDetailUiState.value.copy(
                    task = _selectedTaskDetailUiState.value.task?.copy(isCompleted = isCompleted)
                )
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            if (_selectedTaskDetailUiState.value.task?.id == task.id) {
                _selectedTaskDetailUiState.value = TaskDetailUiState(task = null, isLoading = false) // Indica que a tarefa não existe mais
            }
        }
    }
}

class TaskViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}