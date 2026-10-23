package cr.ac.utn.avistacr.viewmodel

import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Location
import cr.ac.utn.avistacr.util.DateUtils
import cr.ac.utn.avistacr.util.FormField

data class SightingFormUiState(
    val id: Long? = null,
    val species: String = "",
    val scientificName: String = "",
    val type: AnimalType? = null,
    val dateMillis: Long? = DateUtils.todayMillis(),
    val location: Location? = null,
    val notes: String = "",
    val photoUri: String? = null,
    val errors: Map<FormField, Int> = emptyMap(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) {
    val isEditMode: Boolean
        get() = id != null
}