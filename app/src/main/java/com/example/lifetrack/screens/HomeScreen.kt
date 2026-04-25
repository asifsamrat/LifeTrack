package com.example.lifetrack.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lifetrack.navigation.BottomNavItem

@Composable
fun HomeScreen(rootNavController: NavController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Notes,
                    BottomNavItem.Reminder,
                    BottomNavItem.Memories
                )
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        label = { Text(item.title) },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.title)
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = {
                    showMenu = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Add Daily Notes") },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_note")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add Special Notes") },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_special_note")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Special Day Reminder") },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_reminder/Special Day Reminder")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Event Reminder") },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_reminder/Event Reminder")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add Memories") },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_memory")
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) { HomeTab() }
            composable(BottomNavItem.Notes.route) { NotesScreen() }
            composable(BottomNavItem.Reminder.route) { ReminderScreen() }
            composable(BottomNavItem.Memories.route) { MemoriesScreen() }
        }
    }
}

@Composable
fun HomeTab() {
    // User State
    var name by remember { mutableStateOf("Md. Asif Samrat") }
    var age by remember { mutableStateOf("25") }
    var occupation by remember { mutableStateOf("Software Engineer") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    var showEditDialog by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    val upcomingReminders = listOf(
        ReminderItem("Meeting with Team", "10:00 AM"),
        ReminderItem("Doctor Appointment", "02:30 PM"),
        ReminderItem("Grocery Shopping", "06:00 PM"),
        ReminderItem("Call Mom", "08:00 PM")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Left: Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { imageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Small Edit indicator
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(2.dp),
                    tint = Color.White
                )
            }

            // Right: Info
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.clickable { showEditDialog = true }
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Age: $age",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Occupation: $occupation",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Tap to edit info",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Upcoming Reminders Section
        Text(
            text = "Upcoming Reminders",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(upcomingReminders) { reminder ->
                ReminderCard(reminder)
            }
        }
    }

    // Edit Info Dialog
    if (showEditDialog) {
        var tempName by remember { mutableStateOf(name) }
        var tempAge by remember { mutableStateOf(age) }
        var tempOccupation by remember { mutableStateOf(occupation) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile Info") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Name") })
                    OutlinedTextField(value = tempAge, onValueChange = { tempAge = it }, label = { Text("Age") })
                    OutlinedTextField(value = tempOccupation, onValueChange = { tempOccupation = it }, label = { Text("Occupation") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    name = tempName
                    age = tempAge
                    occupation = tempOccupation
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReminderCard(reminder: ReminderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = reminder.title, fontWeight = FontWeight.Medium)
            Text(text = reminder.time, color = MaterialTheme.colorScheme.outline)
        }
    }
}

data class ReminderItem(val title: String, val time: String)
