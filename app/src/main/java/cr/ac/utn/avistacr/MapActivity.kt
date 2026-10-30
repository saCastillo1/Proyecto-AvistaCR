package cr.ac.utn.avistacr

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cr.ac.utn.avistacr.data.memory.RepositoryProvider
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.util.NavigationUtils
import cr.ac.utn.avistacr.util.labelRes
import cr.ac.utn.avistacr.util.showAnimalTypeTag
import cr.ac.utn.avistacr.viewmodel.MapUiState
import cr.ac.utn.avistacr.viewmodel.MapViewModel
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity() {

    private lateinit var viewModel: MapViewModel
    private lateinit var tvCount: TextView
    private lateinit var cardPreview: MaterialCardView
    private lateinit var tvTypePreview: TextView
    private lateinit var tvSpeciesPreview: TextView
    private lateinit var tvPlacePreview: TextView
    private var selectedSightingId: Long = NavigationUtils.NO_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        viewModel = MapViewModel(RepositoryProvider.sightingRepository)

        tvCount = findViewById(R.id.tvCount_Map)
        cardPreview = findViewById(R.id.cardPreview_Map)
        tvTypePreview = findViewById(R.id.tvType_MapPreview)
        tvSpeciesPreview = findViewById(R.id.tvSpecies_MapPreview)
        tvPlacePreview = findViewById(R.id.tvPlace_MapPreview)
        val chipGroupType = findViewById<ChipGroup>(R.id.chipGroupType_Map)
        val fabMyLocation = findViewById<FloatingActionButton>(R.id.fabMyLocation_Map)
        val btnViewPreview = findViewById<Button>(R.id.btnView_MapPreview)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav_Map)

        chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.onTypeSelected(getTypeFromChip(checkedIds.firstOrNull()))
        }

        fabMyLocation.setOnClickListener {
            Toast.makeText(this, getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show()
        }

        btnViewPreview.setOnClickListener {
            if (selectedSightingId != NavigationUtils.NO_ID) {
                NavigationUtils.openActivity(this, SightingDetailActivity::class.java, selectedSightingId)
            }
        }

        bottomNav.selectedItemId = R.id.nav_map_item
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_list_item) {
                finish()
            }
            item.itemId == R.id.nav_map_item
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> showState(state) }
            }
        }
    }

    private fun showState(state: MapUiState) {
        tvCount.text = resources.getQuantityString(
            R.plurals.sighting_count, state.sightings.size, state.sightings.size
        )

        val sighting = state.selectedSighting
        cardPreview.isVisible = sighting != null
        if (sighting != null) {
            selectedSightingId = sighting.id
            tvTypePreview.showAnimalTypeTag(sighting.type)
            tvSpeciesPreview.text = sighting.species
            tvPlacePreview.text = getString(
                R.string.place_and_province,
                sighting.location.placeName,
                getString(sighting.location.province.labelRes())
            )
        }
    }

    private fun getTypeFromChip(chipId: Int?): AnimalType? = when (chipId) {
        R.id.chipBird_Map -> AnimalType.BIRD
        R.id.chipMammal_Map -> AnimalType.MAMMAL
        R.id.chipReptile_Map -> AnimalType.REPTILE
        R.id.chipAmphibian_Map -> AnimalType.AMPHIBIAN
        R.id.chipOther_Map -> AnimalType.OTHER
        else -> null
    }
}