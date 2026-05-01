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
    const val ADD_REMINDER = "add_reminder"
    
    const val NOTIFICATION = "notification"
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()
    val notificationViewModel: NotificationViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val startDestination = if (authViewModel.isUserLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        /* ---------------- AUTH FLOW ---------------- */

        composable(Routes.LOGIN) {
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController, authViewModel)
        }

        composable (Routes.FORGOT){
            ForgotPasswordScreen(navController, authViewModel)
        }

        /* ---------------- HOME SCREEN ---------------- */

        composable(Routes.HOME) {
            HomeScreen(navController, notificationViewModel)
        }

        /* ---------------- ADD SCREENS ---------------- */

        composable(
            "${Routes.ADD_NOTE}?noteType={noteType}&noteId={noteId}",
            arguments = listOf(
                navArgument("noteType") {
                    type = NavType.StringType
                    defaultValue = "Daily Note"
                },
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteType = backStackEntry.arguments?.getString("noteType") ?: "Daily Note"
            val noteId = backStackEntry.arguments?.getString("noteId")
            val noteViewModel: NoteViewModel = viewModel()
            AddNoteScreen(noteType, navController, noteViewModel, noteId)
        }

        composable(Routes.ADD_SPECIAL_NOTE) {
            val noteViewModel: NoteViewModel = viewModel()
            AddNoteScreen("Special Note", navController, noteViewModel)
        }

        composable(
            "${Routes.ADD_REMINDER}?reminderType={reminderType}&reminderId={reminderId}",
            arguments = listOf(
                navArgument("reminderType") {
                    type = NavType.StringType
                    defaultValue = "Event"
                },
                navArgument("reminderId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val reminderType = backStackEntry.arguments?.getString("reminderType") ?: "Event"
            val reminderId = backStackEntry.arguments?.getString("reminderId")
            val reminderViewModel: ReminderViewModel = viewModel()
            AddReminderScreen(reminderType, navController, reminderViewModel, reminderId)
        }

        composable(
            "${Routes.ADD_MEMORY}?memoryId={memoryId}",
            arguments = listOf(navArgument("memoryId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val memoryId = backStackEntry.arguments?.getString("memoryId")
            val memoryViewModel: MemoryViewModel = viewModel()
            AddMemoryScreen(navController, memoryViewModel, memoryId)
        }

        composable(Routes.NOTIFICATION) {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                viewModel = notificationViewModel
            )
        }
    }
}
