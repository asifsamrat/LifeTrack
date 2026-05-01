package com.example.lifetrack.data.repository

import com.example.lifetrack.data.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore

class ReminderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reminderCollection = db.collection("reminders")

    fun saveReminder(reminder: Reminder, onResult: (Boolean, String) -> Unit) {
        val id = if (reminder.id.isEmpty()) reminderCollection.document().id else reminder.id
        val finalReminder = reminder.copy(id = id)
        reminderCollection.document(id).set(finalReminder)
            .addOnSuccessListener {
                onResult(true, "Reminder saved successfully")
            }
            .addOnFailureListener {
                onResult(false, it.message ?: "Failed to save reminder")
            }
    }

    fun deleteReminder(reminderId: String, onResult: (Boolean) -> Unit) {
        if (reminderId.isEmpty()) {
            onResult(false)
            return
        }
        reminderCollection.document(reminderId).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getRemindersByType(userId: String, reminderType: String, onResult: (List<Reminder>) -> Unit) {
        reminderCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("reminderType", reminderType)
            .get()
            .addOnSuccessListener { result ->
                val reminders = result.mapNotNull { document ->
                    document.toObject(Reminder::class.java).copy(id = document.id)
                }
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
                val reminders = result.mapNotNull { document ->
                    document.toObject(Reminder::class.java).copy(id = document.id)
                }
                onResult(reminders)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getReminderById(reminderId: String, onResult: (Reminder?) -> Unit) {
        if (reminderId.isEmpty()) {
            onResult(null)
            return
        }
        reminderCollection.document(reminderId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.toObject(Reminder::class.java)?.copy(id = document.id))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
