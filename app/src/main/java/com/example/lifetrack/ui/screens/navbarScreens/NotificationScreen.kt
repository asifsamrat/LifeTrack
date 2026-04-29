package com.example.lifetrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifetrack.ui.theme.Green
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// ---------------------------
// 🔹 Data Model
// ---------------------------
data class NotificationItem(
    val id: String,
    val title: String,
    val type: String, // reminder / memory / event
    val timestamp: Long
)

// ---------------------------
// 🔹 Main Screen
// ---------------------------
@Composable
fun NotificationScreen(
    onBack: () -> Unit
) {

    // 🔸 Dummy Data (you will replace with ViewModel later)
    val allItems = sampleNotifications()

    // 🔸 Filter expired (last 5 days)
    val expiredItems = getRecentlyExpiredNotifications(allItems)

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(onBack)

        if (expiredItems.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(expiredItems) { item ->
                    NotificationItemUI(item)
                }
            }
        }
    }
}

// ---------------------------
// 🔹 Top Bar
// ---------------------------
@Composable
fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Green)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.clickable { onBack() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Expired Notifications",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ---------------------------
// 🔹 Notification Item UI
// ---------------------------
@Composable
fun NotificationItemUI(item: NotificationItem) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = item.type.uppercase(),
            fontSize = 12.sp,
            color = Color.Gray
        )

        Text(
            text = formatDate(item.timestamp),
            fontSize = 12.sp,
            color = Color.Gray
        )

        Text(
            text = "EXPIRED",
            fontSize = 11.sp,
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}

// ---------------------------
// 🔹 Empty State
// ---------------------------
@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No expired items",
            color = Color.Gray
        )
    }
}

// ---------------------------
// 🔹 Filter Logic (last 5 days expired)
// ---------------------------
fun getRecentlyExpiredNotifications(
    items: List<NotificationItem>
): List<NotificationItem> {

    val now = System.currentTimeMillis()
    val fiveDaysAgo = now - TimeUnit.DAYS.toMillis(5)

    return items
        .filter { it.timestamp in fiveDaysAgo until now }
        .sortedByDescending { it.timestamp }
}

// ---------------------------
// 🔹 Sample Data (CUSTOM)
// ---------------------------
fun sampleNotifications(): List<NotificationItem> {

    val now = System.currentTimeMillis()

    return listOf(
        NotificationItem(
            "1",
            "Missed Meeting",
            "reminder",
            now - TimeUnit.HOURS.toMillis(2)
        ),
        NotificationItem(
            "2",
            "Gym Session",
            "reminder",
            now - TimeUnit.DAYS.toMillis(1)
        ),
        NotificationItem(
            "3",
            "Trip Memory",
            "memory",
            now - TimeUnit.DAYS.toMillis(3)
        ),
        NotificationItem(
            "4",
            "Assignment Deadline",
            "event",
            now - TimeUnit.DAYS.toMillis(6) // ❌ will NOT show (older than 5 days)
        )
    )
}

// ---------------------------
// 🔹 Date Formatter
// ---------------------------
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}