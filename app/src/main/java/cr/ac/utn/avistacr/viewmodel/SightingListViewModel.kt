package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Province
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.model.SightingFilter
import cr.ac.utn.avistacr.domain.model.SortOrder
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import cr.ac.utn.avistacr.util.SightingFilterHelper

class SightingListViewModel(
    private val repository: ISightingRepository
) : ViewModel() {

    var filter: SightingFilter = SightingFilter()
        private set

    fun getSightings(): List<Sighting> {
        return SightingFilterHelper.apply(repository.getSightings(), filter)
    }

    fun setQuery(query: String) {
        filter = filter.copy(query = query)
    }

    fun setType(type: AnimalType?) {
        filter = filter.copy(type = type)
    }

    fun setProvince(province: Province?) {
        filter = filter.copy(province = province)
    }

    fun setDateRange(fromMillis: Long?, toMillis: Long?) {
        filter = filter.copy(fromDateMillis = fromMillis, toDateMillis = toMillis)
    }

    fun setSortOrder(sortOrder: SortOrder) {
        filter = filter.copy(sortOrder = sortOrder)
    }

    fun clearFilters() {
        filter = SightingFilter(sortOrder = filter.sortOrder)
    }

    fun deleteSighting(id: Long) {
        repository.deleteSighting(id)
    }
}