package com.example.lifetrack.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lifetrack.data.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            
            // Reschedule all upcoming reminders from Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("reminders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val reminders = snapshot.toObjects(Reminder::class.java)
                    val now = System.currentTimeMillis()
                    
                    for (reminder in reminders) {
                        val alarmTime = DateTimeUtils.parseToMillis(reminder.date, reminder.time) - 
                                (reminder.remindDays * 86400000L + 
                                 reminder.remindHours * 3600000L + 
                                 reminder.remindMinutes * 60000L)
                        
                        if (alarmTime > now) {
                            AlarmScheduler.scheduleAlarm(context, reminder)
                        }
                    }
                }
        }
    }
}
