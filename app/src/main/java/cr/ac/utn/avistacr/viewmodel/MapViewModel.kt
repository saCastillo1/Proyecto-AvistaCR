package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository

class MapViewModel(
    private val repository: ISightingRepository
) : ViewModel() {

    fun getSightings(type: AnimalType?): List<Sighting> {
        val sightings = repository.getSightings()
        return if (type == null) sightings else sightings.filter { it.type == type }
    }
}