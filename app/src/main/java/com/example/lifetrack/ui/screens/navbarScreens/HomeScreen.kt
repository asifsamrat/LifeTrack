package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifetrack.navigation.BottomNavItem
import com.example.lifetrack.navigation.Routes
import com.example.lifetrack.ui.screens.NotificationScreen
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.viewModel.AuthViewModel
import com.example.lifetrack.viewModel.MemoryViewModel
import com.example.lifetrack.viewModel.NoteViewModel
import com.example.lifetrack.viewModel.NotificationViewModel
import com.example.lifetrack.viewModel.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(rootNavController: NavController, notificationViewModel: NotificationViewModel) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Initialize ViewModels here so they are scoped to HomeScreen
    val noteViewModel: NoteViewModel = viewModel()
    val reminderViewModel: ReminderViewModel = viewModel()
    val memoryViewModel: MemoryViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Start listening for data once when the user is logged in
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            noteViewModel.startListening(userId)
            reminderViewModel.startListening(userId)
            memoryViewModel.startListening(userId)
        }
    }

    var showMenu by remember { mutableStateOf(false) }

    val onSignOut = {
        authViewModel.logout()
        rootNavController.navigate(Routes.LOGIN) {
            popUpTo(Routes.HOME) { inclusive = true }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color.White,
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.shadow(4.dp)
                )
                NavigationBar (
                    containerColor = Color.White,
                    modifier = Modifier.height(100.dp)
                ){
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
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkGreen,
                                selectedTextColor = DarkGreen,
                                unselectedIconColor = GreenLight,
                                unselectedTextColor = GreenLight,
                                indicatorColor = Color.Transparent
                            )
                        )

                    }
                }
            }
        },

        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = {
                        showMenu = true
                    },
                    containerColor = DarkGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color.White)
                        .clip(shape = RoundedCornerShape(10.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add Daily Notes",
                                color = DarkGreen
                            )
                        },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_note?noteType=Daily Note")
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add Special Notes",
                                color = DarkGreen
                            )
                        },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_note?noteType=Special Note")
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Special Day Reminder",
                                color = DarkGreen
                            )
                        },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_reminder?reminderType=Special")
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Event Reminder",
                                color = DarkGreen
                            )
                        },
                        onClick = {
                            showMenu = false
                            rootNavController.navigate("add_reminder?reminderType=Event")
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add Memories",
                                color = DarkGreen
                            )
                        },
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
            composable(BottomNavItem.Home.route) {
                HomeTab(
                    navController = rootNavController, 
                    notificationViewModel = notificationViewModel,
                    noteViewModel = noteViewModel,
                    reminderViewModel = reminderViewModel,
                    memoryViewModel = memoryViewModel,
                    onSignOut = onSignOut
                )
            }

            composable("notification") {
                NotificationScreen(
                    onBack = { rootNavController.popBackStack() },
                    viewModel = notificationViewModel
                )
            }
            composable(BottomNavItem.Notes.route) {
                NotesScreen(
                    rootNavController = rootNavController, 
                    noteViewModel = noteViewModel,
                    notificationViewModel = notificationViewModel,
                    onSignOut = onSignOut
                )
            }
            composable(BottomNavItem.Reminder.route) {
                ReminderScreen(rootNavController, reminderViewModel)
            }
            composable(BottomNavItem.Memories.route) {
                MemoriesScreen(rootNavController, memoryViewModel)
            }
        }
    }
}
