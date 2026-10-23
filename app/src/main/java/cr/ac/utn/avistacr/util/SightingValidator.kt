package cr.ac.utn.avistacr.util

import cr.ac.utn.avistacr.R
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Location

enum class FormField {
    SPECIES,
    TYPE,
    DATE,
    LOCATION,
    NOTES
}

object SightingValidator {
    const val MIN_SPECIES_LENGTH = 3
    const val MAX_NOTES_LENGTH = 500

    fun validate(
        species: String,
        type: AnimalType?,
        dateMillis: Long?,
        location: Location?,
        notes: String
    ): Map<FormField, Int> {
        val errors = mutableMapOf<FormField, Int>()

        when {
            species.isBlank() -> errors[FormField.SPECIES] = R.string.error_species_required
            species.trim().length < MIN_SPECIES_LENGTH ->
                errors[FormField.SPECIES] = R.string.error_species_too_short
        }

        if (type == null) {
            errors[FormField.TYPE] = R.string.error_type_required
        }

        when {
            dateMillis == null -> errors[FormField.DATE] = R.string.error_date_required
            dateMillis > DateUtils.endOfDay(DateUtils.todayMillis()) ->
                errors[FormField.DATE] = R.string.error_date_future
        }

        if (location == null) {
            errors[FormField.LOCATION] = R.string.error_location_required
        }

        if (notes.length > MAX_NOTES_LENGTH) {
            errors[FormField.NOTES] = R.string.error_notes_too_long
        }

        return errors
    }
}