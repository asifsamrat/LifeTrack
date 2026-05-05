package com.example.lifetrack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.NotificationViewModel
import java.util.*

// ---------------------------
// 🔹 Data Model
// ---------------------------
data class NotificationItem(
    val id: String,
    val title: String,
    val type: String, // reminder / memory / event
    val timestamp: Long,
    var isRead: Boolean = false
)

// ---------------------------
// 🔹 Main Screen
// ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    viewModel: NotificationViewModel
) {
    val expiredItems = viewModel.notifications

    // Refresh and Mark all as read when screen is opened
    LaunchedEffect(Unit) {
        viewModel.startListening()
        viewModel.markAllAsRead()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkGreen
                        )
                    }
                },
                actions = {
                    if (expiredItems.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.clearAllNotifications() },
                            modifier = Modifier.padding(end = 8.dp),
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Clear All",
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = white
                )
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (expiredItems.isEmpty()) {
                EmptyState()
            } else {
                Text(
                    text = "Expired Reminders & Passed Memories",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(expiredItems) { item ->
                        NotificationItemUI(
                            item = item,
                            onDelete = { viewModel.deleteNotification(item) }
                        )
                    }
                }
            }
        }
    }
}


// ---------------------------
// 🔹 Notification Item UI
// ---------------------------
@Composable
fun NotificationItemUI(
    item: NotificationItem,
    onDelete: () -> Unit
) {
    val icon = when (item.type.lowercase()) {
        "reminder" -> Icons.Default.Timer
        "memory" -> Icons.Default.PhotoLibrary
        "event" -> Icons.Default.Event
        else -> Icons.Default.NotificationsActive
    }

    val iconBackground = when (item.type.lowercase()) {
        "reminder" -> Color(0xFFE3F2FD)
        "memory" -> Color(0xFFF3E5F5)
        "event" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF1F8E9)
    }

    val iconColor = when (item.type.lowercase()) {
        "reminder" -> Color(0xFF2196F3)
        "memory" -> Color(0xFF9C27B0)
        "event" -> Color(0xFFFF9800)
        else -> DarkGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.175f)
                    .aspectRatio(1f)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = item.type,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(0.6f)
            ) {
                // Showing the type of notification
                Text(
                    text = item.type.replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = DarkGreen,
                    fontWeight = FontWeight.Medium
                )

                // Showing the title of the notification
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Showing the date and time of the notification

                val dateStr = DateTimeUtils.getStorageDate(item.timestamp)
                val timeStr = java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(item.timestamp))

                Text(
                    text = "${DateTimeUtils.formatForDisplay(dateStr)} at ${DateTimeUtils.formatTimeForDisplay(timeStr)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(0.225f),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Text(
                    text = "Remove",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F8E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = GreenLime,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Notifications",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You're all caught up! There are no expired reminders or memories.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
