package cr.ac.utn.avistacr.data.memory

import cr.ac.utn.avistacr.domain.repository.ISightingRepository

object RepositoryProvider {
    val sightingRepository: ISightingRepository by lazy { MemoryDataRepository() }
}