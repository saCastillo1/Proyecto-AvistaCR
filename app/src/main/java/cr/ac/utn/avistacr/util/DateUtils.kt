package cr.ac.utn.avistacr.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val DATE_PATTERN = "dd/MM/yyyy"
    private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L

    fun formatDate(millis: Long): String =
        SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(Date(millis))

    fun toMillis(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance().apply {
            clear()
            set(year, month - 1, day)
        }.timeInMillis

    fun todayMillis(): Long = System.currentTimeMillis()

    fun startOfDay(millis: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun endOfDay(millis: Long): Long = startOfDay(millis) + DAY_IN_MILLIS - 1
}