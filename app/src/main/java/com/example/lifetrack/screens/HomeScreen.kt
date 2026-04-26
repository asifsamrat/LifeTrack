package com.example.lifetrack.screens

import HomeTab
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifetrack.navigation.BottomNavItem
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight

@Composable
fun HomeScreen(rootNavController: NavController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color.White,
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 0.5.dp,
                    modifier = Modifier.shadow(4.dp)
                )
                NavigationBar (
                    containerColor = Color.White,
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
                                Icon(imageVector = item.icon, contentDescription = item.title)
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
                            rootNavController.navigate("add_note")
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
                            rootNavController.navigate("add_special_note")
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
                            rootNavController.navigate("add_reminder/Special Day Reminder")
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
                            rootNavController.navigate("add_reminder/Event Reminder")
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
            composable(BottomNavItem.Home.route) { HomeTab() }
            composable(BottomNavItem.Notes.route) { NotesScreen() }
            composable(BottomNavItem.Reminder.route) { ReminderScreen() }
            composable(BottomNavItem.Memories.route) { MemoriesScreen() }
        }
    }
}


