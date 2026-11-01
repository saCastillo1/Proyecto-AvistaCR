package cr.ac.utn.avistacr.data.memory

import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.repository.ISightingRepository

class MemoryDataRepository : ISightingRepository {
    private val sightings = SampleData.sightings.toMutableList()
    private var nextId: Long = (sightings.maxOfOrNull { it.id } ?: 0) + 1

    override fun getSightings(): List<Sighting> = sightings.toList()

    override fun getById(id: Long): Sighting? = sightings.find { it.id == id }

    override fun addSighting(sighting: Sighting) {
        sightings.add(sighting.copy(id = nextId))
        nextId++
    }

    override fun updateSighting(sighting: Sighting) {
        val index = sightings.indexOfFirst { it.id == sighting.id }
        if (index >= 0) {
            sightings[index] = sighting
        }
    }

    override fun deleteSighting(id: Long) {
        sightings.removeIf { it.id == id }
    }
}