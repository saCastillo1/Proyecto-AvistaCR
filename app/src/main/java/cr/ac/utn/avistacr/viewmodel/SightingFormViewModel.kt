package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Location
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import cr.ac.utn.avistacr.util.FormField
import cr.ac.utn.avistacr.util.SightingValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SightingFormViewModel(private val repository: ISightingRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SightingFormUiState())

    val uiState: StateFlow<SightingFormUiState> = _uiState.asStateFlow()

    fun loadSighting(id: Long) {
        viewModelScope.launch {
            val sighting = repository.getSightingById(id) ?: return@launch
            _uiState.value = SightingFormUiState(
                id = sighting.id,
                species = sighting.species,
                scientificName = sighting.scientificName,
                type = sighting.type,
                dateMillis = sighting.dateMillis,
                location = sighting.location,
                notes = sighting.notes,
                photoUri = sighting.photoUri
            )
        }
    }

    fun onSpeciesChange(value: String) {
        _uiState.update { it.copy(species = value, errors = it.errors - FormField.SPECIES) }
    }

    fun onScientificNameChange(value: String) {
        _uiState.update { it.copy(scientificName = value) }
    }

    fun onTypeChange(type: AnimalType) {
        _uiState.update { it.copy(type = type, errors = it.errors - FormField.TYPE) }
    }

    fun onDateChange(millis: Long) {
        _uiState.update { it.copy(dateMillis = millis, errors = it.errors - FormField.DATE) }
    }

    fun onLocationChange(location: Location?) {
        _uiState.update { it.copy(location = location, errors = it.errors - FormField.LOCATION) }
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value, errors = it.errors - FormField.NOTES) }
    }

    fun onPhotoSelected(uri: String?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun saveSighting() {
        val state = _uiState.value
        val errors = SightingValidator.validate(
            species = state.species,
            type = state.type,
            dateMillis = state.dateMillis,
            location = state.location,
            notes = state.notes
        )
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        val type = state.type ?: return
        val dateMillis = state.dateMillis ?: return
        val location = state.location ?: return

        val sighting = Sighting(
            id = state.id ?: 0,
            species = state.species.trim(),
            scientificName = state.scientificName.trim(),
            type = type,
            dateMillis = dateMillis,
            location = location,
            notes = state.notes.trim(),
            photoUri = state.photoUri
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            if (state.isEditMode) {
                repository.updateSighting(sighting)
            } else {
                repository.addSighting(sighting)
            }
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}