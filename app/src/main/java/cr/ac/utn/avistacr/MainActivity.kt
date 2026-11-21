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
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import cr.ac.utn.avistacr.adapter.SightingAdapter
import cr.ac.utn.avistacr.data.memory.RepositoryProvider
import cr.ac.utn.avistacr.domain.model.AnimalType
import cr.ac.utn.avistacr.domain.model.Province
import cr.ac.utn.avistacr.domain.model.SortOrder
import cr.ac.utn.avistacr.util.DateUtils
import cr.ac.utn.avistacr.util.NavigationUtils
import cr.ac.utn.avistacr.util.labelRes
import cr.ac.utn.avistacr.viewmodel.SightingListViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SightingListViewModel
    private lateinit var adapter: SightingAdapter
    private lateinit var txtSearch: TextInputEditText
    private lateinit var chipGroupType: ChipGroup
    private lateinit var btnProvince: Button
    private lateinit var btnDate: Button
    private lateinit var btnSort: Button
    private lateinit var btnClearFilters: Button
    private lateinit var tvCount: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        viewModel = SightingListViewModel(RepositoryProvider.sightingRepository)

        txtSearch = findViewById(R.id.txtSearch_Main)
        chipGroupType = findViewById(R.id.chipGroupType_Main)
        btnProvince = findViewById(R.id.btnProvince_Main)
        btnDate = findViewById(R.id.btnDate_Main)
        btnSort = findViewById(R.id.btnSort_Main)
        btnClearFilters = findViewById(R.id.btnClearFilters_Main)
        tvCount = findViewById(R.id.tvCount_Main)
        tvEmpty = findViewById(R.id.tvEmpty_Main)
        bottomNav = findViewById(R.id.bottomNav_Main)
        val rvSightings = findViewById<RecyclerView>(R.id.rvSightings_Main)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd_Main)

        adapter = SightingAdapter { sighting ->
            NavigationUtils.openActivity(this, SightingDetailActivity::class.java, sighting.id)
        }
        rvSightings.layoutManager = LinearLayoutManager(this)
        rvSightings.adapter = adapter

        txtSearch.doAfterTextChanged { text ->
            viewModel.setQuery(text?.toString() ?: "")
            refreshList()
        }

        chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.setType(getTypeFromChip(checkedIds.firstOrNull()))
            refreshList()
        }

        btnProvince.setOnClickListener { showProvinceDialog() }
        btnDate.setOnClickListener { showDateRangePicker() }
        btnSort.setOnClickListener { showSortDialog() }

        btnClearFilters.setOnClickListener {
            txtSearch.setText("")
            chipGroupType.check(R.id.chipAll_Main)
            viewModel.clearFilters()
            refreshList()
        }

        fabAdd.setOnClickListener {
            NavigationUtils.openActivity(this, SightingFormActivity::class.java)
        }

        bottomNav.selectedItemId = R.id.nav_list_item
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_map_item) {
                NavigationUtils.openActivity(this, MapActivity::class.java)
            }
            item.itemId == R.id.nav_list_item
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        try {
            val sightings = viewModel.getSightings()
            val filter = viewModel.filter

            adapter.submitList(sightings)
            tvCount.text = resources.getQuantityString(
                R.plurals.sighting_count, sightings.size, sightings.size
            )

            tvEmpty.isVisible = sightings.isEmpty()
            tvEmpty.setText(
                if (filter.isActive) R.string.empty_filter_message
                else R.string.empty_list_message
            )
            btnClearFilters.isVisible = filter.isActive

            val province = filter.province
            btnProvince.text = if (province != null) getString(province.labelRes())
            else getString(R.string.filter_province)

            val from = filter.fromDateMillis
            val to = filter.toDateMillis
            btnDate.text = if (from != null && to != null) {
                getString(R.string.date_range_format, DateUtils.formatDate(from), DateUtils.formatDate(to))
            } else {
                getString(R.string.filter_date)
            }

            btnSort.setText(filter.sortOrder.labelRes())
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getTypeFromChip(chipId: Int?): AnimalType? = when (chipId) {
        R.id.chipBird_Main -> AnimalType.BIRD
        R.id.chipMammal_Main -> AnimalType.MAMMAL
        R.id.chipReptile_Main -> AnimalType.REPTILE
        R.id.chipAmphibian_Main -> AnimalType.AMPHIBIAN
        R.id.chipOther_Main -> AnimalType.OTHER
        else -> null
    }

    private fun showProvinceDialog() {
        val provinces = Province.entries
        val labels = (listOf(getString(R.string.filter_all)) +
                provinces.map { getString(it.labelRes()) }).toTypedArray()
        val current = viewModel.filter.province
        val checkedItem = if (current == null) 0 else provinces.indexOf(current) + 1

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.filter_province)
            .setSingleChoiceItems(labels, checkedItem) { dialog, which ->
                viewModel.setProvince(if (which == 0) null else provinces[which - 1])
                refreshList()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showSortDialog() {
        val sortOrders = SortOrder.entries
        val labels = sortOrders.map { getString(it.labelRes()) }.toTypedArray()
        val checkedItem = sortOrders.indexOf(viewModel.filter.sortOrder)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.sort_title)
            .setSingleChoiceItems(labels, checkedItem) { dialog, which ->
                viewModel.setSortOrder(sortOrders[which])
                refreshList()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showDateRangePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(R.string.filter_date)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val start = selection.first
            val end = selection.second
            if (start != null && end != null) {
                viewModel.setDateRange(
                    DateUtils.fromUtcSelection(start),
                    DateUtils.fromUtcSelection(end)
                )
                refreshList()
            }
        }
        picker.show(supportFragmentManager, "date_range_picker")
    }
}