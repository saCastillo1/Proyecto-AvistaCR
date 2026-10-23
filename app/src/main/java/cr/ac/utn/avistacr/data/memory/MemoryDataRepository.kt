package cr.ac.utn.avistacr.data.memory

import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MemoryDataRepository : ISightingRepository {

    private val sightings = MutableStateFlow(SampleData.sightings)
    private var nextId: Long = (SampleData.sightings.maxOfOrNull { it.id } ?: 0) + 1

    override fun observeSightings(): Flow<List<Sighting>> = sightings.asStateFlow()

    override suspend fun getSightingById(id: Long): Sighting? =
        sightings.value.find { it.id == id }

    override suspend fun addSighting(sighting: Sighting) {
        sightings.update { it + sighting.copy(id = nextId++) }
    }

    override suspend fun updateSighting(sighting: Sighting) {
        sightings.update { list ->
            list.map { if (it.id == sighting.id) sighting else it }
        }
    }

    override suspend fun deleteSighting(sighting: Sighting) {
        sightings.update { list -> list.filterNot { it.id == sighting.id } }
    }
}