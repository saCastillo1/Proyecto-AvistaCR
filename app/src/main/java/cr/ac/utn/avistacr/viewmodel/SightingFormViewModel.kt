package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Location
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import cr.ac.utn.avistacr.util.FormField
import cr.ac.utn.avistacr.util.SightingValidator

class SightingFormViewModel(
    private val repository: ISightingRepository
) : ViewModel() {

    fun getSightingById(id: Long): Sighting? {
        return repository.getById(id)
    }

    fun validate(
        species: String,
        type: AnimalType?,
        dateMillis: Long?,
        location: Location?,
        notes: String
    ): Map<FormField, Int> {
        return SightingValidator.validate(species, type, dateMillis, location, notes)
    }

    fun saveSighting(sighting: Sighting) {
        repository.addSighting(sighting)
    }

    fun updateSighting(sighting: Sighting) {
        repository.updateSighting(sighting)
    }
}