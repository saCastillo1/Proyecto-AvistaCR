package cr.ac.utn.avistacr.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val placeName: String,
    val province: Province
)