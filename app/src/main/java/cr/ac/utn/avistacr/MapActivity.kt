package cr.ac.utn.avistacr

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cr.ac.utn.avistacr.data.memory.RepositoryProvider
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.viewmodel.MapViewModel

class MapActivity : AppCompatActivity() {

    private lateinit var viewModel: MapViewModel
    private lateinit var tvCount: TextView
    private var selectedType: AnimalType? = null

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
        val chipGroupType = findViewById<ChipGroup>(R.id.chipGroupType_Map)
        val fabMyLocation = findViewById<FloatingActionButton>(R.id.fabMyLocation_Map)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav_Map)

        chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedType = getTypeFromChip(checkedIds.firstOrNull())
            refreshMap()
        }

        fabMyLocation.setOnClickListener {
            Toast.makeText(this, getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show()
        }

        bottomNav.selectedItemId = R.id.nav_map_item
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_list_item) {
                finish()
            }
            item.itemId == R.id.nav_map_item
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMap()
    }

    private fun refreshMap() {
        try {
            val sightings = viewModel.getSightings(selectedType)
            tvCount.text = resources.getQuantityString(
                R.plurals.sighting_count, sightings.size, sightings.size
            )
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
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