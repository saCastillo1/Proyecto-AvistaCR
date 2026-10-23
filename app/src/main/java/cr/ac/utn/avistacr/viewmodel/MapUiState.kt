package cr.ac.utn.avistacr.viewmodel

import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Sighting

data class MapUiState(
    val sightings: List<Sighting> = emptyList(),
    val selectedType: AnimalType? = null,
    val selectedSighting: Sighting? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)