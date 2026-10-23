package cr.ac.utn.avistacr.viewmodel

import cr.ac.utn.avistacr.domain.model.Sighting

data class SightingDetailUiState(
    val sighting: Sighting? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null
)