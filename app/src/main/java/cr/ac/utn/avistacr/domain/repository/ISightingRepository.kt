package cr.ac.utn.avistacr.domain.repository

import cr.ac.utn.avistacr.domain.model.Sighting

interface ISightingRepository {
    fun getSightings(): List<Sighting>
    fun getById(id: Long): Sighting?
    fun addSighting(sighting: Sighting)
    fun updateSighting(sighting: Sighting)
    fun deleteSighting(id: Long)
}