package com.example.lifetrack.data.repository

import com.example.lifetrack.data.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore

class ReminderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reminderCollection = db.collection("reminders")

    fun saveReminder(reminder: Reminder, onResult: (Boolean, String) -> Unit) {
        val id = reminderCollection.document().id
        val finalReminder = reminder.copy(id = id)
        reminderCollection.document(id).set(finalReminder)
            .addOnSuccessListener {
                onResult(true, "Reminder saved successfully")
            }
            .addOnFailureListener {
                onResult(false, it.message ?: "Failed to save reminder")
            }
    }

    fun getRemindersByType(userId: String, reminderType: String, onResult: (List<Reminder>) -> Unit) {
        reminderCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("reminderType", reminderType)
            .get()
            .addOnSuccessListener { result ->
                val reminders = result.toObjects(Reminder::class.java)
                onResult(reminders)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getRemindersByUserId(userId: String, onResult: (List<Reminder>) -> Unit) {
        reminderCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val reminders = result.toObjects(Reminder::class.java)
                onResult(reminders)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
