package com.jhreyess.reservoir.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun getCurrentDate(daysOffset: Int = 0): String {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now(ZoneId.of("America/Monterrey"))
            .plusDays(daysOffset.toLong())
            .format(DateTimeFormatter.ISO_DATE)
    } else {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es", "MX"))
        dateFormat.timeZone = TimeZone.getTimeZone("America/Monterrey")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        return dateFormat.format(calendar.time)
    }
}


fun minutesUntilTarget(targetHour: Int, targetMinute: Int): Long {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val now = ZonedDateTime.now(ZoneId.of("America/Monterrey"))

        var targetTime = now.withHour(targetHour).withMinute(targetMinute).withSecond(0).withNano(0)

        // If the target time is before now, move to the next day
        if (targetTime.isBefore(now)) {
            targetTime = targetTime.plusDays(1)
        }

        val duration = Duration.between(now, targetTime)
        duration.toMinutes()
    } else {
        val now = Calendar.getInstance(TimeZone.getTimeZone("America/Monterrey"))
        val target = Calendar.getInstance(TimeZone.getTimeZone("America/Monterrey"))
        target.set(Calendar.HOUR_OF_DAY, targetHour)
        target.set(Calendar.MINUTE, targetMinute)

        if(target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val diff = target.timeInMillis - now.timeInMillis
        return TimeUnit.MILLISECONDS.toMinutes(diff)
    }
}