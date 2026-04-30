package com.example.lifetrack.data.model

data class Reminder(
    val id: String = "",
    val reminderType: String = "", // "Event" or "Special"
    val title: String = "",
    val description: String = "",
    val date: String = "", // yyyy-MM-dd
    val time: String = "", // HH:mm
    val userId: String = "",
    val indicatorColor: String = "Green",
    val remindDays: Int = 0,
    val remindHours: Int = 0,
    val remindMinutes: Int = 0
)
