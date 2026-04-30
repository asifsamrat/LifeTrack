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
        return try {
            val date = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault()).parse(storageDate)
            SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            storageDate
        }
    }

    fun formatTimeForDisplay(storageTime: String): String {
        return try {
            val time = SimpleDateFormat(TIME_FORMAT_STORAGE, Locale.getDefault()).parse(storageTime)
            SimpleDateFormat(TIME_FORMAT_DISPLAY, Locale.getDefault()).format(time!!)
        } catch (e: Exception) {
            storageTime
        }
    }

    fun parseToMillis(date: String, time: String = "00:00"): Long {
        return try {
            val dateTime = "$date $time"
            SimpleDateFormat(DATE_TIME_FORMAT_STORAGE, Locale.getDefault()).parse(dateTime)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
