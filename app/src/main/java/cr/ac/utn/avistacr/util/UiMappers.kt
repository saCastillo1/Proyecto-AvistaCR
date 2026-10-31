package cr.ac.utn.avistacr.util

import androidx.annotation.StringRes
import cr.ac.utn.avistacr.R
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Province
import cr.ac.utn.avistacr.domain.model.SortOrder

@StringRes
fun AnimalType.labelRes(): Int = when (this) {
    AnimalType.BIRD -> R.string.type_bird
    AnimalType.MAMMAL -> R.string.type_mammal
    AnimalType.REPTILE -> R.string.type_reptile
    AnimalType.AMPHIBIAN -> R.string.type_amphibian
    AnimalType.OTHER -> R.string.type_other
}

@StringRes
fun Province.labelRes(): Int = when (this) {
    Province.SAN_JOSE -> R.string.province_san_jose
    Province.ALAJUELA -> R.string.province_alajuela
    Province.CARTAGO -> R.string.province_cartago
    Province.HEREDIA -> R.string.province_heredia
    Province.GUANACASTE -> R.string.province_guanacaste
    Province.PUNTARENAS -> R.string.province_puntarenas
    Province.LIMON -> R.string.province_limon
}

@StringRes
fun SortOrder.labelRes(): Int = when (this) {
    SortOrder.NEWEST -> R.string.sort_newest
    SortOrder.OLDEST -> R.string.sort_oldest
    SortOrder.NAME -> R.string.sort_name
}