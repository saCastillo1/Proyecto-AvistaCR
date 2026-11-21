package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository

class SightingDetailViewModel(
    private val repository: ISightingRepository
) : ViewModel() {

    fun getSightingById(id: Long): Sighting? {
        return repository.getById(id)
    }

    fun deleteSighting(id: Long) {
        repository.deleteSighting(id)
    }
}