package cr.ac.utn.avistacr

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import cr.ac.utn.avistacr.data.memory.RepositoryProvider
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Location
import cr.ac.utn.avistacr.domain.model.Province
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.util.DateUtils
import cr.ac.utn.avistacr.util.FormField
import cr.ac.utn.avistacr.util.NavigationUtils
import cr.ac.utn.avistacr.util.labelRes
import cr.ac.utn.avistacr.viewmodel.SightingFormViewModel
import java.util.Calendar

class SightingFormActivity : AppCompatActivity() {

    private lateinit var viewModel: SightingFormViewModel
    private lateinit var imgPhoto: ImageView
    private lateinit var layoutAddPhoto: LinearLayout
    private lateinit var tilSpecies: TextInputLayout
    private lateinit var txtSpecies: TextInputEditText
    private lateinit var txtScientificName: TextInputEditText
    private lateinit var tilType: TextInputLayout
    private lateinit var actvType: MaterialAutoCompleteTextView
    private lateinit var tilDate: TextInputLayout
    private lateinit var txtDate: TextInputEditText
    private lateinit var tilPlace: TextInputLayout
    private lateinit var txtPlace: TextInputEditText
    private lateinit var actvProvince: MaterialAutoCompleteTextView
    private lateinit var tilNotes: TextInputLayout
    private lateinit var txtNotes: TextInputEditText

    private val animalTypes = AnimalType.entries
    private val provinces = Province.entries
    private var editingSighting: Sighting? = null
    private var selectedType: AnimalType? = null
    private var selectedProvince: Province? = null
    private var selectedDateMillis: Long = DateUtils.todayMillis()
    private var photoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sighting_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = SightingFormViewModel(RepositoryProvider.sightingRepository)

        imgPhoto = findViewById(R.id.imgPhoto_Form)
        layoutAddPhoto = findViewById(R.id.layoutAddPhoto_Form)
        tilSpecies = findViewById(R.id.tilSpecies_Form)
        txtSpecies = findViewById(R.id.txtSpecies_Form)
        txtScientificName = findViewById(R.id.txtScientificName_Form)
        tilType = findViewById(R.id.tilType_Form)
        actvType = findViewById(R.id.actvType_Form)
        tilDate = findViewById(R.id.tilDate_Form)
        txtDate = findViewById(R.id.txtDate_Form)
        tilPlace = findViewById(R.id.tilPlace_Form)
        txtPlace = findViewById(R.id.txtPlace_Form)
        actvProvince = findViewById(R.id.actvProvince_Form)
        tilNotes = findViewById(R.id.tilNotes_Form)
        txtNotes = findViewById(R.id.txtNotes_Form)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_Form)
        val cardPhoto = findViewById<MaterialCardView>(R.id.cardPhoto_Form)
        val btnUseLocation = findViewById<Button>(R.id.btnUseLocation_Form)
        val btnPickOnMap = findViewById<Button>(R.id.btnPickOnMap_Form)
        val btnCancel = findViewById<Button>(R.id.btnCancel_Form)
        val btnSave = findViewById<Button>(R.id.btnSave_Form)

        val sightingId = intent.getLongExtra(NavigationUtils.EXTRA_SIGHTING_ID, NavigationUtils.NO_ID)
        if (sightingId != NavigationUtils.NO_ID) {
            editingSighting = viewModel.getSightingById(sightingId)
        }

        toolbar.setTitle(
            if (editingSighting != null) R.string.title_edit_sighting
            else R.string.title_new_sighting
        )
        toolbar.setNavigationOnClickListener { finish() }

        actvType.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1,
                animalTypes.map { getString(it.labelRes()) })
        )
        actvType.setOnItemClickListener { _, _, position, _ ->
            selectedType = animalTypes[position]
            tilType.error = null
        }

        actvProvince.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1,
                provinces.map { getString(it.labelRes()) })
        )
        actvProvince.setOnItemClickListener { _, _, position, _ ->
            selectedProvince = provinces[position]
        }

        txtDate.setOnClickListener { showDatePicker() }
        tilDate.setEndIconOnClickListener { showDatePicker() }

        cardPhoto.setOnClickListener { showPhotoSourceDialog() }
        btnUseLocation.setOnClickListener { showComingSoon() }
        btnPickOnMap.setOnClickListener { showComingSoon() }
        btnCancel.setOnClickListener { finish() }
        btnSave.setOnClickListener { saveSighting() }

        editingSighting?.let { sighting -> fillForm(sighting) }
        showDate()
        showPhoto()
    }

    private fun fillForm(sighting: Sighting) {
        txtSpecies.setText(sighting.species)
        txtScientificName.setText(sighting.scientificName)
        selectedType = sighting.type
        actvType.setText(getString(sighting.type.labelRes()), false)
        selectedDateMillis = sighting.dateMillis
        selectedProvince = sighting.location.province
        actvProvince.setText(getString(sighting.location.province.labelRes()), false)
        txtPlace.setText(sighting.location.placeName)
        txtNotes.setText(sighting.notes)
        photoUri = sighting.photoUri
    }

    private fun TextInputEditText.value(): String = text?.toString()?.trim() ?: ""

    private fun saveSighting() {
        try {
            val species = txtSpecies.value()
            val scientificName = txtScientificName.value()
            val place = txtPlace.value()
            val notes = txtNotes.value()
            val province = selectedProvince

            val location = if (place.isNotEmpty() && province != null) {
                Location(
                    latitude = editingSighting?.location?.latitude ?: 0.0,
                    longitude = editingSighting?.location?.longitude ?: 0.0,
                    placeName = place,
                    province = province
                )
            } else {
                null
            }

            val errors = viewModel.validate(species, selectedType, selectedDateMillis, location, notes)
            showErrors(errors)

            val type = selectedType
            if (errors.isNotEmpty() || type == null || location == null) {
                return
            }

            val sighting = Sighting(
                id = editingSighting?.id ?: 0,
                species = species,
                scientificName = scientificName,
                type = type,
                dateMillis = selectedDateMillis,
                location = location,
                notes = notes,
                photoUri = photoUri
            )

            if (editingSighting == null) {
                viewModel.saveSighting(sighting)
                Toast.makeText(this, getString(R.string.msg_sighting_saved), Toast.LENGTH_LONG).show()
            } else {
                viewModel.updateSighting(sighting)
                Toast.makeText(this, getString(R.string.msg_sighting_updated), Toast.LENGTH_LONG).show()
            }
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun showErrors(errors: Map<FormField, Int>) {
        tilSpecies.error = errors[FormField.SPECIES]?.let { getString(it) }
        tilType.error = errors[FormField.TYPE]?.let { getString(it) }
        tilDate.error = errors[FormField.DATE]?.let { getString(it) }
        tilPlace.error = errors[FormField.LOCATION]?.let { getString(it) }
        tilNotes.error = errors[FormField.NOTES]?.let { getString(it) }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDateMillis = DateUtils.toMillis(year, month + 1, dayOfMonth)
                tilDate.error = null
                showDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showDate() {
        txtDate.setText(DateUtils.formatDate(selectedDateMillis))
    }

    private fun showPhoto() {
        val uri = photoUri
        if (uri != null) {
            imgPhoto.setImageURI(Uri.parse(uri))
            imgPhoto.isVisible = true
            layoutAddPhoto.isVisible = false
        } else {
            imgPhoto.isVisible = false
            layoutAddPhoto.isVisible = true
        }
    }

    private fun showPhotoSourceDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_photo_source)

        dialog.findViewById<View>(R.id.optionCamera_Dialog)?.setOnClickListener {
            dialog.dismiss()
            showComingSoon()
        }
        dialog.findViewById<View>(R.id.optionGallery_Dialog)?.setOnClickListener {
            dialog.dismiss()
            showComingSoon()
        }
        dialog.findViewById<View>(R.id.optionCancel_Dialog)?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showComingSoon() {
        Toast.makeText(this, getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show()
    }
}