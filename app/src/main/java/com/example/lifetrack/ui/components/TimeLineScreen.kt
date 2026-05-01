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
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.MemoryViewModel
import com.example.lifetrack.viewModel.NoteViewModel
import com.example.lifetrack.viewModel.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

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
            val now = System.currentTimeMillis()
            
            // Fetch Reminders: Filter non-expired and sort ascending
            reminderViewModel.getRemindersByType(userId, "Event") { events ->
                reminderViewModel.getRemindersByType(userId, "Special") { specials ->
                    upcomingReminders = (events + specials)
                        .filter { DateTimeUtils.parseToMillis(it.date, it.time) >= now }
                        .sortedBy { DateTimeUtils.parseToMillis(it.date, it.time) }
                }
            }
            
            // Fetch Memories: Filter non-expired and sort ascending
            memoryViewModel.getMemoriesByUserId(userId) { memories ->
                upcomingMemories = memories
                    .filter { DateTimeUtils.parseToMillis(it.date, it.time) >= now }
                    .sortedBy { DateTimeUtils.parseToMillis(it.date, it.time) }
                    .take(3)
            }
            
            // Fetch Notes: Sorted by date descending
            noteViewModel.getNotesByType(userId, "Daily Note") { daily ->
                noteViewModel.getNotesByType(userId, "Special Note") { special ->
                    recentNotes = (daily + special)
                        .sortedByDescending { DateTimeUtils.parseToMillis(it.date) }
                        .take(3)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp, top = 12.dp, end = 8.dp, bottom = 8.dp)
    ) {
        // Reminders Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, null, tint = DarkGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upcoming Reminders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp).padding(horizontal = 12.dp)
        ) {
            if (upcomingReminders.isEmpty()) {
                Text("No upcoming reminders", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
            } else {
                upcomingReminders.forEach { TimeLineReminderCard(it) }
            }
        }

        // Memories Section
        Row(modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PhotoLibrary, null, tint = DarkGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upcoming Memories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp).padding(horizontal = 12.dp)
        ) {
            if (upcomingMemories.isEmpty()) {
                Text("No upcoming memories", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
            } else {
                upcomingMemories.forEach { TimeLineMemoriesCard(it) }
            }
        }

        // Notes Section
        Row(modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Edit, null, tint = DarkGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Recent Notes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp).padding(horizontal = 12.dp)
        ) {
            if (recentNotes.isEmpty()) {
                Text("No recent notes", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
            } else {
                recentNotes.forEach { TimeLineNoteCard(it) }
            }
        }
    }
}

fun getRemainingTime(deadlineMillis: Long): String {
    val currentMillis = System.currentTimeMillis()
    val diff = deadlineMillis - currentMillis

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
