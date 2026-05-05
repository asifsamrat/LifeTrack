package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.R
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController, viewModel: ReminderViewModel) {
    var selectedTab by remember { mutableStateOf("Event") }
    
    // Use cached reminders from ViewModel
    val allReminders by viewModel.reminders

    val reminderList = remember(allReminders, selectedTab) {
        val now = System.currentTimeMillis()
        if (selectedTab == "Event") {
            // One-time events: Filter future only, sort by actual date
            allReminders
                .filter { it.reminderType == "Event" }
                .filter { DateTimeUtils.parseToMillis(it.date, it.time) >= now }
                .sortedBy { DateTimeUtils.parseToMillis(it.date, it.time) }
        } else {
            // Special Day Reminders: Circular (yearly), sort by nearest future occurrence
            allReminders
                .filter { it.reminderType == "Special" }
                .sortedBy { DateTimeUtils.getNextOccurrence(it.date, it.time) }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.life_track_logo_transperant),
                            contentDescription = "Logo",
                            modifier = Modifier.size(60.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "My Reminders",
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = white
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(white)
        ) {
            // Tab Selection Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                ReminderTabButton(
                    text = "Event Reminder",
                    isSelected = selectedTab == "Event",
                    onClick = { selectedTab = "Event" },
                    modifier = Modifier.weight(1f)
                )
                ReminderTabButton(
                    text = "Special Day Reminder",
                    isSelected = selectedTab == "Special",
                    onClick = { selectedTab = "Special" },
                    modifier = Modifier.weight(1f)
                )
            }

            // Reminders List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(reminderList) { reminder ->
                    if (selectedTab == "Event") {
                        EventReminderItem(
                            reminder = reminder,
                            onEdit = {
                                navController.navigate("add_reminder?reminderType=${reminder.reminderType}&reminderId=${reminder.id}")
                            },
                            onDelete = {
                                viewModel.deleteReminder(reminder.id)
                            }
                        )
                    } else {
                        SpecialDayReminderItem(
                            reminder = reminder,
                            onEdit = {
                                navController.navigate("add_reminder?reminderType=${reminder.reminderType}&reminderId=${reminder.id}")
                            },
                            onDelete = {
                                viewModel.deleteReminder(reminder.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = if (isSelected) DarkGreen else GreenLight,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

@Composable
fun EventReminderItem(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = white)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = reminder.reminderType,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )

                Text(
                    text = "${DateTimeUtils.formatForDisplay(reminder.date)} at ${DateTimeUtils.formatTimeForDisplay(reminder.time)}",
                    fontSize = 14.sp,
                    color = GreenLime
                )
            }

            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = DarkGreen)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(color = white)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = DarkGreen) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SpecialDayReminderItem(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    val nextOccur = remember(reminder) { DateTimeUtils.getNextOccurrence(reminder.date, reminder.time) }
    val displayDate = remember(nextOccur) { DateTimeUtils.getStorageDate(nextOccur) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = white)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cake,
                    contentDescription = reminder.reminderType,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )

                Text(
                    text = "Next: ${DateTimeUtils.formatForDisplay(displayDate)} at ${DateTimeUtils.formatTimeForDisplay(reminder.time)}",
                    fontSize = 14.sp,
                    color = GreenLime
                )
                
                Text(
                    text = "Yearly Circular Reminder",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = DarkGreen)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(color = white)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = DarkGreen) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
