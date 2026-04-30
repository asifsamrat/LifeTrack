package com.example.lifetrack.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    // Storage formats (sortable and easy for calculations)
    private const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    private const val TIME_FORMAT_STORAGE = "HH:mm"
    private const val DATE_TIME_FORMAT_STORAGE = "yyyy-MM-dd HH:mm"

    // Display formats (user-friendly)
    private const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    private const val TIME_FORMAT_DISPLAY = "hh:mm a"

    fun getStorageDate(millis: Long): String =
        SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).format(Date(millis))

    fun getStorageTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return SimpleDateFormat(TIME_FORMAT_STORAGE, Locale.getDefault()).format(calendar.time)
    }

    fun formatForDisplay(storageDate: String): String {
        if (storageDate.isEmpty()) return ""
        return try {
            // Try parsing as storage format first
            val date = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).parse(storageDate)
            SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            // If it's already in display format, return as is
            storageDate
        }
    }

    fun formatTimeForDisplay(storageTime: String): String {
        if (storageTime.isEmpty()) return ""
        return try {
            val time = SimpleDateFormat(TIME_FORMAT_STORAGE, Locale.getDefault()).parse(storageTime)
            SimpleDateFormat(TIME_FORMAT_DISPLAY, Locale.getDefault()).format(time!!)
        } catch (e: Exception) {
            storageTime
        }
    }

    fun parseToMillis(date: String, time: String = "00:00"): Long {
        if (date.isEmpty()) return 0L
        
        val formats = listOf(
            "yyyy-MM-dd HH:mm",       // New storage format
            "MMM dd, yyyy hh:mm a",   // Legacy Reminder format
            "MMM dd, yyyy HH:mm",      // Legacy Note format
            "yyyy-MM-dd"              // Date only
        )

        val timeToUse = if (time.isEmpty()) "00:00" else time
        val dateTimeString = "$date $timeToUse"

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val parsedDate = sdf.parse(dateTimeString)
                if (parsedDate != null) return parsedDate.time
            } catch (e: Exception) {
                continue
            }
        }
        
        // Final fallback: try parsing just the date if the combined string failed
        try {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.time ?: 0L
        } catch (e: Exception) {
            try {
                return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(date)?.time ?: 0L
            } catch (e2: Exception) {
                return 0L
            }
        }
    }
}
