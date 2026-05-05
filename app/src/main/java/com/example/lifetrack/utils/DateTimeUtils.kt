package com.example.lifetrack.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    // Storage formats (sortable and easy for calculations)
    private const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    private const val TIME_FORMAT_STORAGE = "HH:mm"

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
            val date = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).parse(storageDate)
            SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
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
            "yyyy-MM-dd HH:mm",
            "MMM dd, yyyy hh:mm a",
            "MMM dd, yyyy HH:mm",
            "yyyy-MM-dd"
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

    /**
     * Calculates the next occurrence of a given date (month and day) from today onwards.
     * This is used for circular yearly reminders/memories.
     */
    fun getNextOccurrence(storageDate: String, storageTime: String = "00:00"): Long {
        if (storageDate.isEmpty()) return Long.MAX_VALUE
        try {
            val calInput = Calendar.getInstance().apply {
                val date = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).parse(storageDate)
                if (date != null) time = date
            }
            val timeParts = storageTime.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            
            val now = Calendar.getInstance()
            val nextOccur = Calendar.getInstance().apply {
                set(Calendar.MONTH, calInput.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, calInput.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            if (nextOccur.before(now)) {
                nextOccur.add(Calendar.YEAR, 1)
            }
            
            return nextOccur.timeInMillis
        } catch (e: Exception) {
            return Long.MAX_VALUE
        }
    }

    /**
     * Gets the most recent occurrence of a yearly repeating date (either this year if passed, or last year).
     */
    fun getMostRecentOccurrence(storageDate: String, storageTime: String = "00:00"): Long {
        if (storageDate.isEmpty()) return 0L
        try {
            val calInput = Calendar.getInstance().apply {
                val date = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).parse(storageDate)
                if (date != null) time = date
            }
            val timeParts = storageTime.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            
            val now = Calendar.getInstance()
            val thisYearOccur = Calendar.getInstance().apply {
                set(Calendar.MONTH, calInput.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, calInput.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (thisYearOccur.after(now)) {
                thisYearOccur.add(Calendar.YEAR, -1)
            }
            
            return thisYearOccur.timeInMillis
        } catch (e: Exception) {
            return 0L
        }
    }

    /**
     * Gets the occurrence of a yearly repeating date in the current year.
     */
    fun getAnniversaryThisYear(storageDate: String, storageTime: String = "00:00"): Long {
        if (storageDate.isEmpty()) return 0L
        try {
            val calInput = Calendar.getInstance().apply {
                val date = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).parse(storageDate)
                if (date != null) time = date
            }
            val timeParts = storageTime.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            
            val anniversary = Calendar.getInstance().apply {
                set(Calendar.MONTH, calInput.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, calInput.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return anniversary.timeInMillis
        } catch (e: Exception) {
            return 0L
        }
    }
}
