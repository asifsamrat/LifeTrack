package com.example.lifetrack.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.lifetrack.ui.screens.NotificationItem
import java.util.concurrent.TimeUnit

class NotificationViewModel : ViewModel() {
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> get() = _notifications

    init {
        // Initialize with sample data
        val now = System.currentTimeMillis()
        _notifications.addAll(
            listOf(
                NotificationItem("1", "Business Strategy Meeting", "reminder", now - TimeUnit.HOURS.toMillis(2), isRead = false),
                NotificationItem("2", "Weekend Hiking Trip", "event", now - TimeUnit.DAYS.toMillis(1), isRead = false),
                NotificationItem("3", "Family Reunion Photos", "memory", now - TimeUnit.DAYS.toMillis(3), isRead = false),
                NotificationItem("4", "Buy Groceries", "reminder", now - TimeUnit.DAYS.toMillis(4), isRead = false)
            )
        )
    }

    // Use derivedStateOf to ensure Compose tracks changes to the _notifications list
    val unreadCount: Int by derivedStateOf {
        _notifications.count { !it.isRead && isRecentlyExpired(it) }
    }

    fun markAllAsRead() {
        _notifications.forEachIndexed { index, item ->
            if (!item.isRead && isRecentlyExpired(item)) {
                _notifications[index] = item.copy(isRead = true)
            }
        }
    }

    private fun isRecentlyExpired(item: NotificationItem): Boolean {
        val now = System.currentTimeMillis()
        val fiveDaysAgo = now - TimeUnit.DAYS.toMillis(5)
        return item.timestamp in fiveDaysAgo until now
    }
}
