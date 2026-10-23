package cr.ac.utn.avistacr.domain.repository

import cr.ac.utn.avistacr.domain.model.Sighting
import kotlinx.coroutines.flow.Flow

interface ISightingRepository {
    fun observeSightings(): Flow<List<Sighting>>

    suspend fun getSightingById(id: Long): Sighting?

    suspend fun addSighting(sighting: Sighting)

    suspend fun updateSighting(sighting: Sighting)

    suspend fun deleteSighting(sighting: Sighting)
}