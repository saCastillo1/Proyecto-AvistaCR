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
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import cr.ac.utn.avistacr.util.DateUtils
import cr.ac.utn.avistacr.util.FormField
import cr.ac.utn.avistacr.util.NavigationUtils
import cr.ac.utn.avistacr.util.labelRes
import cr.ac.utn.avistacr.viewmodel.SightingFormUiState
import cr.ac.utn.avistacr.viewmodel.SightingFormViewModel
import kotlinx.coroutines.launch
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
    private var selectedProvince: Province? = null
    private var isFormFilled = false

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
        val isEditMode = sightingId != NavigationUtils.NO_ID

        toolbar.setTitle(if (isEditMode) R.string.title_edit_sighting else R.string.title_new_sighting)
        toolbar.setNavigationOnClickListener { finish() }

        actvType.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1,
                animalTypes.map { getString(it.labelRes()) })
        )
        actvType.setOnItemClickListener { _, _, position, _ ->
            viewModel.onTypeChange(animalTypes[position])
        }

        actvProvince.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1,
                provinces.map { getString(it.labelRes()) })
        )
        actvProvince.setOnItemClickListener { _, _, position, _ ->
            selectedProvince = provinces[position]
            updateLocation()
        }

        txtSpecies.doAfterTextChanged { text ->
            viewModel.onSpeciesChange(text?.toString() ?: "")
        }
        txtScientificName.doAfterTextChanged { text ->
            viewModel.onScientificNameChange(text?.toString() ?: "")
        }
        txtPlace.doAfterTextChanged { updateLocation() }
        txtNotes.doAfterTextChanged { text ->
            viewModel.onNotesChange(text?.toString() ?: "")
        }

        txtDate.setOnClickListener { showDatePicker() }
        tilDate.setEndIconOnClickListener { showDatePicker() }

        cardPhoto.setOnClickListener { showPhotoSourceDialog() }
        btnUseLocation.setOnClickListener { showComingSoon() }
        btnPickOnMap.setOnClickListener { showComingSoon() }
        btnCancel.setOnClickListener { finish() }
        btnSave.setOnClickListener { viewModel.saveSighting() }

        if (isEditMode) {
            viewModel.loadSighting(sightingId)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> showState(state) }
            }
        }
    }

    private fun showState(state: SightingFormUiState) {
        if (isFinishing) return

        if (state.isSaved) {
            val message = if (state.isEditMode) R.string.msg_sighting_updated
            else R.string.msg_sighting_saved
            Toast.makeText(this, getString(message), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (state.isEditMode && !isFormFilled) {
            isFormFilled = true
            fillForm(state)
        }

        txtDate.setText(state.dateMillis?.let { DateUtils.formatDate(it) } ?: "")
        showPhoto(state.photoUri)

        tilSpecies.error = state.errors[FormField.SPECIES]?.let { getString(it) }
        tilType.error = state.errors[FormField.TYPE]?.let { getString(it) }
        tilDate.error = state.errors[FormField.DATE]?.let { getString(it) }
        tilPlace.error = state.errors[FormField.LOCATION]?.let { getString(it) }
        tilNotes.error = state.errors[FormField.NOTES]?.let { getString(it) }
    }

    private fun fillForm(state: SightingFormUiState) {
        txtSpecies.setText(state.species)
        txtScientificName.setText(state.scientificName)
        state.type?.let { type ->
            actvType.setText(getString(type.labelRes()), false)
        }
        state.location?.let { location ->
            selectedProvince = location.province
            actvProvince.setText(getString(location.province.labelRes()), false)
            txtPlace.setText(location.placeName)
        }
        txtNotes.setText(state.notes)
    }

    private fun updateLocation() {
        val place = txtPlace.text?.toString()?.trim() ?: ""
        val province = selectedProvince
        val currentLocation = viewModel.uiState.value.location

        if (place.isNotEmpty() && province != null) {
            viewModel.onLocationChange(
                Location(
                    latitude = currentLocation?.latitude ?: 0.0,
                    longitude = currentLocation?.longitude ?: 0.0,
                    placeName = place,
                    province = province
                )
            )
        } else {
            viewModel.onLocationChange(null)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = viewModel.uiState.value.dateMillis ?: DateUtils.todayMillis()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                viewModel.onDateChange(DateUtils.toMillis(year, month + 1, dayOfMonth))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showPhoto(photoUri: String?) {
        if (photoUri != null) {
            imgPhoto.setImageURI(Uri.parse(photoUri))
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