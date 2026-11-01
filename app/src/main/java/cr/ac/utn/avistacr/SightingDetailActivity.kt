package cr.ac.utn.avistacr

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cr.ac.utn.avistacr.data.memory.RepositoryProvider
import cr.ac.utn.avistacr.util.DateUtils
import cr.ac.utn.avistacr.util.NavigationUtils
import cr.ac.utn.avistacr.util.labelRes
import cr.ac.utn.avistacr.util.showAnimalTypeTag
import cr.ac.utn.avistacr.viewmodel.SightingDetailViewModel

class SightingDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: SightingDetailViewModel
    private lateinit var imgPhoto: ImageView
    private lateinit var tvType: TextView
    private lateinit var tvSpecies: TextView
    private lateinit var tvScientificName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvNotes: TextView
    private var sightingId: Long = NavigationUtils.NO_ID
    private var currentSpecies: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sighting_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = SightingDetailViewModel(RepositoryProvider.sightingRepository)
        sightingId = intent.getLongExtra(NavigationUtils.EXTRA_SIGHTING_ID, NavigationUtils.NO_ID)

        imgPhoto = findViewById(R.id.imgPhoto_Detail)
        tvType = findViewById(R.id.tvType_Detail)
        tvSpecies = findViewById(R.id.tvSpecies_Detail)
        tvScientificName = findViewById(R.id.tvScientificName_Detail)
        tvDate = findViewById(R.id.tvDate_Detail)
        tvLocation = findViewById(R.id.tvLocation_Detail)
        tvNotes = findViewById(R.id.tvNotes_Detail)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_Detail)
        val btnViewOnMap = findViewById<Button>(R.id.btnViewOnMap_Detail)

        toolbar.setNavigationOnClickListener { finish() }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit_item -> {
                    NavigationUtils.openActivity(this, SightingFormActivity::class.java, sightingId)
                    true
                }
                R.id.action_delete_item -> {
                    showDeleteDialog()
                    true
                }
                else -> false
            }
        }

        btnViewOnMap.setOnClickListener {
            NavigationUtils.openActivity(this, MapActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        loadSighting()
    }

    private fun loadSighting() {
        try {
            val sighting = viewModel.getSightingById(sightingId)
            if (sighting == null) {
                Toast.makeText(this, getString(R.string.error_sighting_not_found), Toast.LENGTH_LONG).show()
                finish()
                return
            }

            currentSpecies = sighting.species
            tvType.showAnimalTypeTag(sighting.type)
            tvSpecies.text = sighting.species
            tvScientificName.text = sighting.scientificName
            tvScientificName.isVisible = sighting.scientificName.isNotBlank()
            tvDate.text = DateUtils.formatDate(sighting.dateMillis)
            tvLocation.text = getString(
                R.string.place_and_province,
                sighting.location.placeName,
                getString(sighting.location.province.labelRes())
            )
            tvNotes.text = sighting.notes.ifBlank { "-" }

            val photoUri = sighting.photoUri
            if (photoUri != null) {
                imgPhoto.setImageURI(Uri.parse(photoUri))
            } else {
                imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(this)
            .setIcon(android.R.drawable.ic_menu_delete)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, currentSpecies))
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ -> deleteSighting() }
            .show()
    }

    private fun deleteSighting() {
        try {
            viewModel.deleteSighting(sightingId)
            Toast.makeText(this, getString(R.string.msg_sighting_deleted), Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
}