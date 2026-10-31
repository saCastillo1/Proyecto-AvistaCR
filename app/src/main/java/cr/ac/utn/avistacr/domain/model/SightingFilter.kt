package cr.ac.utn.avistacr.domain.model

data class SightingFilter(
    val query: String = "",
    val type: AnimalType? = null,
    val province: Province? = null,
    val fromDateMillis: Long? = null,
    val toDateMillis: Long? = null,
    val sortOrder: SortOrder = SortOrder.NEWEST
) {

    val isActive: Boolean
        get() = query.isNotBlank() ||
                type != null ||
                province != null ||
                fromDateMillis != null ||
                toDateMillis != null
}