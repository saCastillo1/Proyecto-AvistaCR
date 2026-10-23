package cr.ac.utn.avistacr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Province
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.domain.model.SightingFilter
import cr.ac.utn.avistacr.domain.model.SortOrder
import cr.ac.utn.avistacr.domain.repository.ISightingRepository
import cr.ac.utn.avistacr.util.SightingFilterHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SightingListViewModel(private val repository: ISightingRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SightingListUiState(isLoading = true))

    val uiState: StateFlow<SightingListUiState> = _uiState.asStateFlow()

    private var allSightings: List<Sighting> = emptyList()

    init {
        observeSightings()
    }

    private fun observeSightings() {
        viewModelScope.launch {
            repository.observeSightings()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
                .collect { sightings ->
                    allSightings = sightings
                    applyFilter()
                }
        }
    }

    fun onQueryChange(query: String) {
        updateFilter { it.copy(query = query) }
    }

    fun onTypeSelected(type: AnimalType?) {
        updateFilter { it.copy(type = type) }
    }

    fun onProvinceSelected(province: Province?) {
        updateFilter { it.copy(province = province) }
    }

    fun onDateRangeSelected(fromMillis: Long?, toMillis: Long?) {
        updateFilter { it.copy(fromDateMillis = fromMillis, toDateMillis = toMillis) }
    }

    fun onSortOrderSelected(sortOrder: SortOrder) {
        updateFilter { it.copy(sortOrder = sortOrder) }
    }

    fun clearFilters() {
        updateFilter { SightingFilter(sortOrder = it.sortOrder) }
    }

    fun deleteSighting(sighting: Sighting) {
        viewModelScope.launch { repository.deleteSighting(sighting) }
    }

    private fun updateFilter(change: (SightingFilter) -> SightingFilter) {
        _uiState.update { it.copy(filter = change(it.filter)) }
        applyFilter()
    }

    private fun applyFilter() {
        _uiState.update {
            it.copy(
                sightings = SightingFilterHelper.apply(allSightings, it.filter),
                totalCount = allSightings.size,
                isLoading = false
            )
        }
    }
}