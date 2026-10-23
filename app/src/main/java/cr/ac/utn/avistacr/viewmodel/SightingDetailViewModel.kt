package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SightingDetailViewModel(private val repository: ISightingRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SightingDetailUiState())

    val uiState: StateFlow<SightingDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadSighting(id: Long) {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true) }
        loadJob = viewModelScope.launch {
            repository.observeSightings()
                .map { sightings -> sightings.find { it.id == id } }
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
                .collect { sighting ->
                    _uiState.update { it.copy(sighting = sighting, isLoading = false) }
                }
        }
    }

    fun deleteSighting() {
        val sighting = _uiState.value.sighting ?: return
        viewModelScope.launch {
            repository.deleteSighting(sighting)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}