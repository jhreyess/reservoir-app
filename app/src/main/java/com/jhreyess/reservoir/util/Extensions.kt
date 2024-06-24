package com.jhreyess.reservoir.util

import android.os.Build
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun String.asTimestamp(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es","MX"))
    dateFormat.timeZone = TimeZone.getTimeZone("America/Monterrey")
    return try {
        dateFormat.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}

fun String.asUTF8(): String {
    val utf8Bytes = this.unaccented().toByteArray(StandardCharsets.ISO_8859_1)
    return String(utf8Bytes, StandardCharsets.UTF_8).unaccented()
}

fun String.unaccented(): String {
    return this.map { char ->
        when (char) {
            'á', 'Á' -> 'a'
            'é', 'É' -> 'e'
            'í', 'Í' -> 'i'
            'ó', 'Ó' -> 'o'
            'ú', 'Ú' -> 'u'
            else -> char
        }
    }.joinToString("")
}

fun Float.formatDecimals(): String {
    return if(this % 1 == 0f) this.toInt().toString() else "%.2f".format(this)
}

fun Long.relativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        seconds < 60 -> "unos momentos"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        else -> "${days}d "
    }
}

fun getCurrentDate(daysOffset: Int = 0): String {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now(ZoneId.of("America/Monterrey"))
            .plusDays(daysOffset.toLong())
            .format(DateTimeFormatter.ISO_DATE)
    } else {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es","MX"))
        dateFormat.timeZone = TimeZone.getTimeZone("America/Monterrey")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        return dateFormat.format(calendar.time)
    }
}