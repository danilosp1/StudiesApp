package com.example.studies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studies.data.dao.DisciplineWithSchedules
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.MaterialLinkEntity
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.data.model.TaskEntity
import com.example.studies.data.remote.MotdResponse
import com.example.studies.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DisciplinesListUiState(
    val disciplines: List<DisciplineEntity> = emptyList()
)

data class DisciplinesWithSchedulesUiState(
    val disciplines: List<DisciplineWithSchedules> = emptyList()
)

data class SelectedDisciplineDetailState(
    val disciplineWithSchedules: DisciplineWithSchedules? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val materialLinks: List<MaterialLinkEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class DisciplineViewModel(private val repository: AppRepository) : ViewModel() {

    val disciplinesUiState: StateFlow<DisciplinesListUiState> =
        repository.getAllDisciplines()
            .map { DisciplinesListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = DisciplinesListUiState()
            )

    val disciplinesWithSchedulesUiState: StateFlow<DisciplinesWithSchedulesUiState> =
        repository.getAllDisciplinesWithSchedules()
            .map { DisciplinesWithSchedulesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = DisciplinesWithSchedulesUiState()
            )

    private val _selectedDisciplineDetailState = MutableStateFlow(SelectedDisciplineDetailState())
    val selectedDisciplineDetailState: StateFlow<SelectedDisciplineDetailState> = _selectedDisciplineDetailState.asStateFlow()

    private var currentSelectedDisciplineId: Long? = null
    private val activeDisciplineJobs = mutableMapOf<Long, kotlinx.coroutines.Job>()

    private val _messageOfTheDay = MutableStateFlow<String?>(null)
    val messageOfTheDay: StateFlow<String?> = _messageOfTheDay.asStateFlow()

    private val _motdError = MutableStateFlow<String?>(null)
    val motdError: StateFlow<String?> = _motdError.asStateFlow()


    fun loadDisciplineDetailsById(disciplineId: Long) {
        if (disciplineId == -1L) {
            _selectedDisciplineDetailState.value = SelectedDisciplineDetailState(isLoading = false, errorMessage = "ID da disciplina inválido")
            currentSelectedDisciplineId = null
            activeDisciplineJobs[disciplineId]?.cancel()
            return
        }

        if (disciplineId == currentSelectedDisciplineId && !_selectedDisciplineDetailState.value.isLoading && _selectedDisciplineDetailState.value.errorMessage == null) {
            return
        }

        currentSelectedDisciplineId = disciplineId
        activeDisciplineJobs[disciplineId]?.cancel()

        activeDisciplineJobs[disciplineId] = viewModelScope.launch {
            _selectedDisciplineDetailState.value = SelectedDisciplineDetailState(isLoading = true)
            try {
                val disciplineFlow = repository.getDisciplineWithSchedulesById(disciplineId)
                val tasksFlow = repository.getTasksByDiscipline(disciplineId)
                val linksFlow = repository.getMaterialLinksByDiscipline(disciplineId)

                combine(disciplineFlow, tasksFlow, linksFlow) { disciplineDetails, tasks, links ->
                    if (currentSelectedDisciplineId != disciplineId) throw kotlinx.coroutines.CancellationException("Stale data request")
                    SelectedDisciplineDetailState(
                        disciplineWithSchedules = disciplineDetails,
                        tasks = tasks,
                        materialLinks = links,
                        isLoading = false
                    )
                }.catch { e ->
                    if (e !is kotlinx.coroutines.CancellationException && currentSelectedDisciplineId == disciplineId) {
                        _selectedDisciplineDetailState.value = SelectedDisciplineDetailState(isLoading = false, errorMessage = e.message ?: "Erro ao carregar detalhes")
                    }
                }.collectLatest { combinedState ->
                    if (currentSelectedDisciplineId == disciplineId) {
                        _selectedDisciplineDetailState.value = combinedState
                        if (combinedState.disciplineWithSchedules == null && !combinedState.isLoading) {
                            _selectedDisciplineDetailState.value = _selectedDisciplineDetailState.value.copy(errorMessage = "Disciplina não encontrada")
                        }
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException && currentSelectedDisciplineId == disciplineId) {
                    _selectedDisciplineDetailState.value = SelectedDisciplineDetailState(isLoading = false, errorMessage = e.message ?: "Erro geral")
                }
            }
        }
    }


    fun addDiscipline(name: String, location: String?, professor: String?, schedules: List<SubjectScheduleEntity>) {
        viewModelScope.launch {
            val discipline = DisciplineEntity(
                name = name,
                location = location,
                professor = professor
            )
            repository.insertDisciplineWithSchedules(discipline, schedules)
        }
    }

    fun addMaterialLinkToSelectedDiscipline(url: String, description: String?) {
        selectedDisciplineDetailState.value.disciplineWithSchedules?.discipline?.id?.let { discId ->
            viewModelScope.launch {
                if (url.isNotBlank()) {
                    val newLink = MaterialLinkEntity(
                        disciplineId = discId,
                        url = url,
                        description = description?.takeIf { it.isNotBlank() }
                    )
                    repository.insertMaterialLink(newLink)
                }
            }
        }
    }

    fun deleteMaterialLink(link: MaterialLinkEntity) {
        viewModelScope.launch {
            repository.deleteMaterialLink(link)
        }
    }

    fun deleteSelectedDiscipline(onDeleted: () -> Unit) {
        selectedDisciplineDetailState.value.disciplineWithSchedules?.discipline?.let {
            viewModelScope.launch {
                repository.deleteDiscipline(it)
                _selectedDisciplineDetailState.value = SelectedDisciplineDetailState(isLoading=false)
                currentSelectedDisciplineId = null
                onDeleted()
            }
        }
    }

    fun fetchMessageOfTheDay() {
        viewModelScope.launch {
            _motdError.value = null
            _messageOfTheDay.value = null
            try {
                repository.getMessageOfTheDay().collect { motdResponse ->
                    _messageOfTheDay.value = motdResponse.title
                }
            } catch (e: Exception) {
                _motdError.value = "Failed to fetch message: ${e.message}"
            }
        }
    }
}

class DisciplineViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DisciplineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DisciplineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}