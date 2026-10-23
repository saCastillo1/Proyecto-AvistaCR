package cr.ac.utn.avistacr.util

import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.model.SightingFilter
import cr.ac.utn.avistacr.domain.model.SortOrder

object SightingFilterHelper {

    fun apply(sightings: List<Sighting>, filter: SightingFilter): List<Sighting> {
        val query = filter.query.trim()
        val from = filter.fromDateMillis?.let { DateUtils.startOfDay(it) }
        val to = filter.toDateMillis?.let { DateUtils.endOfDay(it) }

        val filtered = sightings.filter { sighting ->
            matchesQuery(sighting, query) &&
                    (filter.type == null || sighting.type == filter.type) &&
                    (filter.province == null || sighting.location.province == filter.province) &&
                    (from == null || sighting.dateMillis >= from) &&
                    (to == null || sighting.dateMillis <= to)
        }

        return when (filter.sortOrder) {
            SortOrder.NEWEST -> filtered.sortedByDescending { it.dateMillis }
            SortOrder.OLDEST -> filtered.sortedBy { it.dateMillis }
            SortOrder.NAME -> filtered.sortedBy { it.species.lowercase() }
        }
    }

    private fun matchesQuery(sighting: Sighting, query: String): Boolean =
        query.isEmpty() ||
                sighting.species.contains(query, ignoreCase = true) ||
                sighting.scientificName.contains(query, ignoreCase = true) ||
                sighting.location.placeName.contains(query, ignoreCase = true)
}