package cr.ac.utn.avistacr.data.memory

import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Location
import cr.ac.utn.avistacr.domain.model.Province
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.util.DateUtils

object SampleData {
    val sightings: List<Sighting> = listOf(
        Sighting(
            id = 1,
            species = "Keel-billed toucan",
            scientificName = "Ramphastos sulfuratus",
            type = AnimalType.BIRD,
            dateMillis = DateUtils.toMillis(2026, 10, 12),
            location = Location(10.3010, -84.8255, "Monteverde", Province.PUNTARENAS),
            notes = "A pair feeding on a guarumo tree near the main trail."
        ),
        Sighting(
            id = 2,
            species = "Mantled howler monkey",
            scientificName = "Alouatta palliata",
            type = AnimalType.MAMMAL,
            dateMillis = DateUtils.toMillis(2026, 10, 10),
            location = Location(9.9068, -84.5262, "Orotina", Province.ALAJUELA),
            notes = "Group of five resting in the trees by the river."
        ),
        Sighting(
            id = 3,
            species = "Green iguana",
            scientificName = "Iguana iguana",
            type = AnimalType.REPTILE,
            dateMillis = DateUtils.toMillis(2026, 10, 5),
            location = Location(9.7719, -84.6266, "Tarcoles", Province.PUNTARENAS),
            notes = "Sunbathing on the rocks next to the bridge."
        ),
        Sighting(
            id = 4,
            species = "Red-eyed tree frog",
            scientificName = "Agalychnis callidryas",
            type = AnimalType.AMPHIBIAN,
            dateMillis = DateUtils.toMillis(2026, 9, 28),
            location = Location(10.4520, -84.0130, "Sarapiqui", Province.HEREDIA),
            notes = "Seen at night after the rain."
        )
    )
}