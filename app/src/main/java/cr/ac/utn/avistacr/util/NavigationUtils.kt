package cr.ac.utn.avistacr.util

import android.content.Context
import android.content.Intent

object NavigationUtils {
    const val EXTRA_SIGHTING_ID = "extra_sighting_id"
    const val NO_ID = -1L

    fun openActivity(context: Context, activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }

    fun openActivity(context: Context, activityClass: Class<*>, sightingId: Long) {
        val intent = Intent(context, activityClass)
        intent.putExtra(EXTRA_SIGHTING_ID, sightingId)
        context.startActivity(intent)
    }
}