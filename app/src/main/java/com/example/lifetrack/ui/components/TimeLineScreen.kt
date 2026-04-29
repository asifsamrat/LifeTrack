package com.example.lifetrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.data.model.Note
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.ui.components.cards.TimeLineMemoriesCard
import com.example.lifetrack.ui.components.cards.TimeLineNoteCard
import com.example.lifetrack.ui.components.cards.TimeLineReminderCard
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.viewModel.MemoryViewModel
import com.example.lifetrack.viewModel.NoteViewModel
import com.example.lifetrack.viewModel.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TimeLineScreen(
    reminderViewModel: ReminderViewModel = viewModel(),
    memoryViewModel: MemoryViewModel = viewModel(),
    noteViewModel: NoteViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var upcomingReminders by remember { mutableStateOf<List<Reminder>>(emptyList()) }
    var upcomingMemories by remember { mutableStateOf<List<Memory>>(emptyList()) }
    var recentNotes by remember { mutableStateOf<List<Note>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            // Fetch Reminders using the correct stored types "Event" and "Special"
            reminderViewModel.getRemindersByType(userId, "Event") { events ->
                reminderViewModel.getRemindersByType(userId, "Special") { specials ->
                    upcomingReminders = (events + specials).sortedBy { it.date }.take(3)
                }
            }
            
            // Fetch Memories
            memoryViewModel.getMemoriesByUserId(userId) { memories ->
                upcomingMemories = memories.sortedByDescending { it.date }.take(3)
            }
            
            // Fetch Notes
            noteViewModel.getNotesByType(userId, "Daily Note") { daily ->
                noteViewModel.getNotesByType(userId, "Special Note") { special ->
                    recentNotes = (daily + special).sortedByDescending { it.date }.take(3)
                }
            }
        }
    }

    // Upcoming reminder and memories
    Column(
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 10.dp)
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Reminder",
                tint = DarkGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Upcoming Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }


        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp)
                .padding(horizontal = 12.dp)
        ) {
            upcomingReminders.forEach {
                TimeLineReminderCard(it)
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "Memories",
                tint = DarkGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Recent Memories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp)
                .padding(horizontal = 12.dp)
        ) {
            upcomingMemories.forEach {
                TimeLineMemoriesCard(it)
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Note",
                tint = DarkGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Recent Notes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp)
                .padding(horizontal = 12.dp)
        ) {
            recentNotes.forEach {
                TimeLineNoteCard(it)
            }
        }
    }
}


fun getRemainingTime(deadlineMillis: Long): String {
    val currentMillis = System.currentTimeMillis()
    var diff = deadlineMillis - currentMillis

    if (diff <= 0) return "Time's up"

    val seconds = diff / 1000 % 60
    val minutes = diff / (1000 * 60) % 60
    val hours = diff / (1000 * 60 * 60) % 24
    val days = diff / (1000 * 60 * 60 * 24)

    return "${days}d ${hours}h ${minutes}m ${seconds}s"
}

@Composable
fun rememberCountdown(deadlineMillis: Long): State<String> {
    val time = remember { mutableStateOf(getRemainingTime(deadlineMillis)) }

    LaunchedEffect(deadlineMillis) {
        while (true) {
            time.value = getRemainingTime(deadlineMillis)
            delay(1000L)
        }
    }

    return time
}

// Utility to convert Date/Time string to Millis for countdown
fun parseDateTimeToMillis(date: String, time: String): Long {
    return try {
        // Updated to match "MMM dd, yyyy hh:mm a" (e.g. "Oct 27, 2025 08:00 PM")
        val format = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        val dateTime = "$date $time"
        format.parse(dateTime)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}
