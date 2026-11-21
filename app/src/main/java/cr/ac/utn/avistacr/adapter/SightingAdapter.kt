package cr.ac.utn.avistacr.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.ac.utn.avistacr.R
import cr.ac.utn.avistacr.domain.model.Sighting
import cr.ac.utn.avistacr.util.DateUtils
import cr.ac.utn.avistacr.util.labelRes
import cr.ac.utn.avistacr.util.showAnimalTypeTag

class SightingAdapter(
    private val onItemClick: (Sighting) -> Unit
) : RecyclerView.Adapter<SightingAdapter.SightingViewHolder>() {

    private var sightings: List<Sighting> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newSightings: List<Sighting>) {
        sightings = newSightings
        notifyDataSetChanged()
    }

    class SightingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto_Item)
        val tvType: TextView = view.findViewById(R.id.tvType_Item)
        val tvSpecies: TextView = view.findViewById(R.id.tvSpecies_Item)
        val tvPlace: TextView = view.findViewById(R.id.tvPlace_Item)
        val tvDate: TextView = view.findViewById(R.id.tvDate_Item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SightingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sighting, parent, false)
        return SightingViewHolder(view)
    }

    override fun getItemCount(): Int = sightings.size

    override fun onBindViewHolder(holder: SightingViewHolder, position: Int) {
        val sighting = sightings[position]
        val context = holder.itemView.context

        holder.tvType.showAnimalTypeTag(sighting.type)
        holder.tvSpecies.text = sighting.species
        holder.tvPlace.text = context.getString(
            R.string.place_and_province,
            sighting.location.placeName,
            context.getString(sighting.location.province.labelRes())
        )
        holder.tvDate.text = DateUtils.formatDate(sighting.dateMillis)

        val photoUri = sighting.photoUri
        if (photoUri != null) {
            holder.imgPhoto.setImageURI(Uri.parse(photoUri))
        } else {
            holder.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener { onItemClick(sighting) }
    }
}