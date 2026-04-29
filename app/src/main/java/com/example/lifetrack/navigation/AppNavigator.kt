package com.example.lifetrack.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lifetrack.ui.screens.authenticationScreens.*
import com.example.lifetrack.ui.screens.navbarScreens.HomeScreen
import com.example.lifetrack.ui.screens.editors.*
import com.example.lifetrack.viewModel.AuthViewModel
import com.example.lifetrack.viewModel.MemoryViewModel
import com.example.lifetrack.viewModel.NoteViewModel
import com.example.lifetrack.viewModel.ReminderViewModel
import com.example.lifetrack.ui.screens.NotificationScreen
import com.example.lifetrack.viewModel.NotificationViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val HOME = "home_main"

    const val ADD_NOTE = "add_note"
    const val ADD_SPECIAL_NOTE = "add_special_note"

    const val ADD_MEMORY = "add_memory"

    const val ADD_REMINDER = "add_reminder/{title}"
    const val ADD_SPECIAL_REMINDER = "add_special_reminder"
    const val ADD_EVENT_REMINDER = "add_event_reminder"
    const val NOTIFICATION = "notification"
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()
    val notificationViewModel: NotificationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        /* ---------------- AUTH FLOW ---------------- */

        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.REGISTER) {
            val authViewModel: AuthViewModel = viewModel()
            RegisterScreen(navController, authViewModel)
        }

        composable(Routes.FORGOT) {
            val authViewModel: AuthViewModel = viewModel()
            ForgotPasswordScreen(navController, authViewModel)
        }

        /* ---------------- MAIN ENTRY ---------------- */

        composable(Routes.HOME) {
            HomeScreen(navController, notificationViewModel)
        }

        /* ---------------- ADD SCREENS ---------------- */

        composable(Routes.ADD_NOTE) {
            val noteViewModel: NoteViewModel = viewModel()
            AddNoteScreen("Daily Note", navController, noteViewModel)
        }

        composable(Routes.ADD_SPECIAL_NOTE) {
            val noteViewModel: NoteViewModel = viewModel()
            AddNoteScreen("Special Note", navController, noteViewModel)
        }

        composable(
            Routes.ADD_REMINDER,
            arguments = listOf(navArgument("title") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val reminderViewModel: ReminderViewModel = viewModel()
            AddReminderScreen(title, navController, reminderViewModel)
        }

        composable(Routes.ADD_SPECIAL_REMINDER) {
            val reminderViewModel: ReminderViewModel = viewModel()
            AddReminderScreen("Special Day Reminder", navController, reminderViewModel)
        }

        composable(Routes.ADD_EVENT_REMINDER) {
            val reminderViewModel: ReminderViewModel = viewModel()
            AddReminderScreen("Event Reminder", navController, reminderViewModel)
        }

        composable(Routes.ADD_MEMORY) {
            val memoryViewModel: MemoryViewModel = viewModel()
            AddMemoryScreen(navController, memoryViewModel)
        }

        composable(Routes.NOTIFICATION) {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                viewModel = notificationViewModel
            )
        }
    }
}
