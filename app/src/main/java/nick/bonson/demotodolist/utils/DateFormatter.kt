package nick.bonson.demotodolist.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {
    private val formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withLocale(Locale.getDefault())

    fun format(epochMillis: Long): String =
        Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
}
