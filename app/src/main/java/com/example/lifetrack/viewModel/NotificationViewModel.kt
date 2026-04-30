package com.example.lifetrack.viewModel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.ui.screens.NotificationItem
import com.example.lifetrack.utils.DateTimeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val prefs = application.getSharedPreferences("lifetrack_notifications", Context.MODE_PRIVATE)
    
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> get() = _notifications

    private var reminderListener: ListenerRegistration? = null
    private var memoryListener: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    private fun getLastReadTimestamp(): Long {
        val userId = getUserId()
        if (userId.isEmpty()) return 0L
        
        val lastRead = prefs.getLong("last_read_$userId", -1L)
        if (lastRead == -1L) {
            // New login: set last read to now so they don't see old history as unread
            val now = System.currentTimeMillis()
            prefs.edit().putLong("last_read_$userId", now).apply()
            return now
        }
        return lastRead
    }

    private fun setLastReadTimestamp(timestamp: Long) {
        val userId = getUserId()
        if (userId.isNotEmpty()) {
            prefs.edit().putLong("last_read_$userId", timestamp).apply()
        }
    }

    fun startListening() {
        val userId = getUserId()
        if (userId.isEmpty()) return

        // Clean up existing listeners
        stopListening()

        // Listen for Reminders
        reminderListener = db.collection("reminders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val reminders = snapshot?.toObjects(Reminder::class.java) ?: emptyList()
                updateList(reminders, null)
            }

        // Listen for Memories
        memoryListener = db.collection("memories")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val memories = snapshot?.toObjects(Memory::class.java) ?: emptyList()
                updateList(null, memories)
            }
    }

    private var cachedReminders = listOf<Reminder>()
    private var cachedMemories = listOf<Memory>()

    @Synchronized
    private fun updateList(reminders: List<Reminder>?, memories: List<Memory>?) {
        if (reminders != null) cachedReminders = reminders
        if (memories != null) cachedMemories = memories

        val lastRead = getLastReadTimestamp()
        val newList = mutableListOf<NotificationItem>()
        val now = System.currentTimeMillis()

        cachedReminders.forEach { reminder ->
            val timestamp = DateTimeUtils.parseToMillis(reminder.date, reminder.time)
            if (timestamp > 0 && timestamp < now) {
                newList.add(
                    NotificationItem(
                        id = reminder.id,
                        title = reminder.title,
                        type = "reminder",
                        timestamp = timestamp,
                        isRead = timestamp <= lastRead
                    )
                )
            }
        }

        cachedMemories.forEach { memory ->
            val timestamp = DateTimeUtils.parseToMillis(memory.date, memory.time)
            if (timestamp > 0 && timestamp < now) {
                newList.add(
                    NotificationItem(
                        id = memory.title + memory.date,
                        title = memory.title,
                        type = "memory",
                        timestamp = timestamp,
                        isRead = timestamp <= lastRead
                    )
                )
            }
        }

        _notifications.clear()
        _notifications.addAll(newList.sortedByDescending { it.timestamp })
    }

    val unreadCount: Int by derivedStateOf {
        _notifications.count { !it.isRead }
    }

    fun markAllAsRead() {
        val now = System.currentTimeMillis()
        setLastReadTimestamp(now)
        
        // Update local state to reflect badge change immediately
        _notifications.forEachIndexed { index, item ->
            if (!item.isRead) {
                _notifications[index] = item.copy(isRead = true)
            }
        }
    }

    fun stopListening() {
        reminderListener?.remove()
        memoryListener?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
