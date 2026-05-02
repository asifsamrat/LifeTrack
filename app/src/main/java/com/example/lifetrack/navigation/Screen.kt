package com.example.lifetrack.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifetrack.ui.screens.navbarScreens.MemoriesScreen
import com.example.lifetrack.ui.screens.navbarScreens.NotesScreen
import com.example.lifetrack.ui.screens.navbarScreens.ReminderScreen
import com.example.lifetrack.viewModel.AuthViewModel
import com.example.lifetrack.viewModel.MemoryViewModel
import com.example.lifetrack.viewModel.NoteViewModel
import com.example.lifetrack.viewModel.NotificationViewModel
import com.example.lifetrack.viewModel.ReminderViewModel

@Composable
fun HomeScreen(rootNavController: NavController) {

    val bottomNavController = rememberNavController()
    val notificationViewModel: NotificationViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val onSignOut = {
        authViewModel.logout()
        rootNavController.navigate(Routes.LOGIN) {
            popUpTo(Routes.HOME) { inclusive = true }
        }
    }

    NavHost(
        navController = bottomNavController,
        startDestination = BottomNavItem.Home.route
    ) {

        composable(BottomNavItem.Home.route) {
            // Avoid recursion by showing a placeholder or the actual HomeTab
            Text("Home Screen Content")
        }

        composable(BottomNavItem.Notes.route) {
            val noteViewModel: NoteViewModel = viewModel()
            NotesScreen(
                rootNavController = rootNavController,
                noteViewModel = noteViewModel,
                notificationViewModel = notificationViewModel,
                onSignOut = onSignOut
            )
        }

        composable(BottomNavItem.Reminder.route) {
            val reminderViewModel: ReminderViewModel = viewModel()
            ReminderScreen(rootNavController, reminderViewModel)
        }

        composable(BottomNavItem.Memories.route) {
            val memoryViewModel: MemoryViewModel = viewModel()
            MemoriesScreen(rootNavController, memoryViewModel)
        }
    }
}
