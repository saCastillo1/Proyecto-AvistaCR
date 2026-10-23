package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(private val repository: ISightingRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))

    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var allSightings: List<Sighting> = emptyList()

    init {
        observeSightings()
    }

    private fun observeSightings() {
        viewModelScope.launch {
            repository.observeSightings()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
                .collect { sightings ->
                    allSightings = sightings
                    applyTypeFilter()
                }
        }
    }

    fun onTypeSelected(type: AnimalType?) {
        _uiState.update { it.copy(selectedType = type, selectedSighting = null) }
        applyTypeFilter()
    }

    fun onMarkerSelected(sighting: Sighting) {
        _uiState.update { it.copy(selectedSighting = sighting) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedSighting = null) }
    }

    private fun applyTypeFilter() {
        _uiState.update { state ->
            state.copy(
                sightings = if (state.selectedType == null) allSightings
                else allSightings.filter { it.type == state.selectedType },
                isLoading = false
            )
        }
    }
}