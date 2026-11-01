package cr.ac.utn.avistacr.util

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
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

@ColorRes
fun AnimalType.containerColorRes(): Int = when (this) {
    AnimalType.BIRD -> R.color.type_bird_container
    AnimalType.MAMMAL -> R.color.type_mammal_container
    AnimalType.REPTILE -> R.color.type_reptile_container
    AnimalType.AMPHIBIAN -> R.color.type_amphibian_container
    AnimalType.OTHER -> R.color.type_other_container
}

@ColorRes
fun AnimalType.onContainerColorRes(): Int = when (this) {
    AnimalType.BIRD -> R.color.type_bird_on_container
    AnimalType.MAMMAL -> R.color.type_mammal_on_container
    AnimalType.REPTILE -> R.color.type_reptile_on_container
    AnimalType.AMPHIBIAN -> R.color.type_amphibian_on_container
    AnimalType.OTHER -> R.color.type_other_on_container
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

fun TextView.showAnimalTypeTag(type: AnimalType) {
    text = context.getString(type.labelRes())
    backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(context, type.containerColorRes())
    )
    setTextColor(ContextCompat.getColor(context, type.onContainerColorRes()))
}