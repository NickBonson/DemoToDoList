package nick.bonson.demotodolist.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun format(date: Date): String = formatter.format(date)
}
