package com.example.lifetrack.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.data.repository.ReminderRepository

class ReminderViewModel : ViewModel() {
    private val reminderRepository = ReminderRepository()

    var message = mutableStateOf("")
        private set
    var isSuccess = mutableStateOf(false)
        private set
    var isSaving = mutableStateOf(false)
        private set

    fun saveReminder(reminder: Reminder) {
        isSaving.value = true
        reminderRepository.saveReminder(reminder) { success, msg ->
            isSaving.value = false
            message.value = msg
            isSuccess.value = success
        }
    }

    fun getRemindersByType(userId: String, reminderType: String, onResult: (List<Reminder>) -> Unit) {
        reminderRepository.getRemindersByType(userId, reminderType) { reminders ->
            onResult(reminders)
        }
    }

    fun resetState() {
        message.value = ""
        isSuccess.value = false
    }
}
