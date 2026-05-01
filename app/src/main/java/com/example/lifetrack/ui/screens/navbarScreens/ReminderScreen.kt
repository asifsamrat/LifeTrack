package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
        allReminders
            .filter { it.reminderType == selectedTab }
            .filter { reminder ->
                val timestamp = DateTimeUtils.parseToMillis(reminder.date, reminder.time)
                timestamp >= now // Only non-expired
            }
            .sortedBy { reminder ->
                DateTimeUtils.parseToMillis(reminder.date, reminder.time) // Sorted order
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
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val color = when(reminder.indicatorColor) {
        "Red" -> Color.Red
        "Yellow" -> Color.Yellow
        "Blue" -> Color.Blue
        else -> Color.Green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reminder.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .then(if (reminder.indicatorColor == "Red") Modifier.alpha(alpha) else Modifier)
                            .background(color = color, shape = CircleShape)
                    )
                }
                
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = DarkGreen)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
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

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${DateTimeUtils.formatForDisplay(reminder.date)} at ${DateTimeUtils.formatTimeForDisplay(reminder.time)}",
                fontSize = 14.sp,
                color = GreenLime
            )
            
            if (reminder.remindDays > 0 || reminder.remindHours > 0 || reminder.remindMinutes > 0) {
                Text(
                    text = "Remind me: ${reminder.remindDays}d ${reminder.remindHours}h ${reminder.remindMinutes}m before",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reminder.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Cake,
                        contentDescription = "Birthday Icon",
                        tint = DarkGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = DarkGreen)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
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

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = DateTimeUtils.formatForDisplay(reminder.date),
                fontSize = 14.sp,
                color = GreenLime
            )
            
            if (reminder.remindDays > 0 || reminder.remindHours > 0 || reminder.remindMinutes > 0) {
                Text(
                    text = "Remind me: ${reminder.remindDays}d ${reminder.remindHours}h ${reminder.remindMinutes}m before",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
