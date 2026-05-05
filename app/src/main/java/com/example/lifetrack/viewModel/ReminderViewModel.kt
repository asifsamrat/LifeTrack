package com.example.lifetrack.viewModel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.data.repository.ReminderRepository
import com.example.lifetrack.utils.AlarmScheduler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val reminderRepository = ReminderRepository()
    private val db = FirebaseFirestore.getInstance()
    private var reminderListener: ListenerRegistration? = null

    private val _reminders = mutableStateOf<List<Reminder>>(emptyList())
    val reminders: State<List<Reminder>> = _reminders

    var message = mutableStateOf("")
        private set
    var isSuccess = mutableStateOf(false)
        private set
    var isSaving = mutableStateOf(false)
        private set

    fun startListening(userId: String) {
        if (reminderListener != null) return
        
        reminderListener = db.collection("reminders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Reminder::class.java)?.copy(id = doc.id)
                    }
                    _reminders.value = list
                    
                    // Optional: Reschedule alarms for all upcoming reminders to ensure they are set
                    // for (reminder in list) {
                    //    AlarmScheduler.scheduleAlarm(getApplication(), reminder)
                    // }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        reminderListener?.remove()
    }

    fun saveReminder(reminder: Reminder) {
        isSaving.value = true
        reminderRepository.saveReminder(reminder) { success, msg ->
            isSaving.value = false
            message.value = msg
            isSuccess.value = success
            if (success) {
                // Schedule the alarm locally
                AlarmScheduler.scheduleAlarm(getApplication(), reminder)
            }
        }
    }

    fun deleteReminder(reminderId: String, onComplete: (Boolean) -> Unit = {}) {
        // Cancel alarm before deleting from DB
        AlarmScheduler.cancelAlarm(getApplication(), reminderId)

        reminderRepository.deleteReminder(reminderId) { success ->
            message.value = if (success) "Reminder deleted successfully" else "Failed to delete reminder"
            onComplete(success)
        }
    }

    fun getReminderById(reminderId: String, onResult: (Reminder?) -> Unit) {
        val cached = _reminders.value.find { it.id == reminderId }
        if (cached != null) {
            onResult(cached)
        } else {
            reminderRepository.getReminderById(reminderId, onResult)
        }
    }

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}
