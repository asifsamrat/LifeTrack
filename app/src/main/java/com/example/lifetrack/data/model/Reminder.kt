package com.example.lifetrack.data.model

data class Reminder(
    val id: String = "",
    val reminderType: String = "", // "Event" or "Special"
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val userId: String = "",
    val indicatorColor: String = "Green" // For Event Reminders: "Red", "Green", "Yellow", "Blue"
)