package com.example.lifetrack.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

object BottomRoutes {
    const val HOME = "home_tab"
    const val NOTES = "notes_tab"
    const val REMINDER = "reminder_tab"
    const val MEMORIES = "memories_tab"
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home_tab", "Home", Icons.Default.Home)
    object Notes : BottomNavItem("notes_tab", "Notes", Icons.Default.Edit)
    object Reminder : BottomNavItem("reminder_tab", "Reminders", Icons.Default.Notifications)
    object Memories : BottomNavItem("memories_tab", "Memories", Icons.Default.Image)
}