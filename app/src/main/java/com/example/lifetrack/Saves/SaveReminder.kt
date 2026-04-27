package com.example.lifetrack.Saves

import com.example.lifetrack.data.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore

fun saveReminder(reminder: Reminder, onComplete: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val reminderRef = db.collection("reminders").document()
    val finalReminder = reminder.copy(id = reminderRef.id)

    reminderRef.set(finalReminder)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}
