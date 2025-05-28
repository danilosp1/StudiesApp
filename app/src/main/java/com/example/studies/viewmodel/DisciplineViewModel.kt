package com.example.studies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studies.data.model.DisciplineEntity
import com.example.studies.data.model.SubjectScheduleEntity
import com.example.studies.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DisciplineUiState(
    val disciplines: List<DisciplineEntity> = emptyList()
)

class DisciplineViewModel(private val repository: AppRepository) : ViewModel() {
    val disciplinesUiState: StateFlow<DisciplineUiState> =
        repository.getAllDisciplines()
            .map { DisciplineUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = DisciplineUiState()
            )

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

}

class DisciplineViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DisciplineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DisciplineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}