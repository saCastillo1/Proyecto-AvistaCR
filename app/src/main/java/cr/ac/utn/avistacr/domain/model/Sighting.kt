package cr.ac.utn.avistacr.domain.model

data class Sighting(
    val id: Long = 0,
    val species: String,
    val scientificName: String = "",
    val type: AnimalType,
    val dateMillis: Long,
    val location: Location,
    val notes: String = "",
    val photoUri: String? = null
)