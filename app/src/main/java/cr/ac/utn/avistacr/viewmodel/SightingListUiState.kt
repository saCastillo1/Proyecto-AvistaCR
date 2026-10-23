package cr.ac.utn.avistacr.viewmodel

import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.model.SightingFilter

data class SightingListUiState(
    val sightings: List<Sighting> = emptyList(),
    val filter: SightingFilter = SightingFilter(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)