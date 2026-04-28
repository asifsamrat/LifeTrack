package com.example.lifetrack.navigation

import AuthViewModel
import MemoryViewModel
import NoteViewModel
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lifetrack.ui.screens.editors.AddMemoryScreen
import com.example.lifetrack.ui.screens.editors.AddNoteScreen
import com.example.lifetrack.ui.screens.editors.AddReminderScreen
import com.example.lifetrack.ui.screens.authenticationScreens.ForgotPasswordScreen
import com.example.lifetrack.ui.screens.navbarScreens.HomeScreen
import com.example.lifetrack.ui.screens.authenticationScreens.LoginScreen
import com.example.lifetrack.ui.screens.authenticationScreens.RegisterScreen

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val authViewModel = AuthViewModel()
    val memoryViewModel = MemoryViewModel()
    val noteViewModel = NoteViewModel()

    NavHost(
        navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Screen.Forgot.route) {
            ForgotPasswordScreen(navController, authViewModel)
        }

        composable(Screen.HomeMain.route) {
            HomeScreen(rootNavController = navController)
        }

        // Add notes button
        composable("add_note") {
            AddNoteScreen("Daily Note",navController, noteViewModel)
        }
        composable("add_special_note") {
            AddNoteScreen("Special Note", navController, noteViewModel)
        }
        
        composable(
            route = "add_reminder/{initialTitle}",
            arguments = listOf(navArgument("initialTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val initialTitle = backStackEntry.arguments?.getString("initialTitle") ?: ""
            AddReminderScreen(initialTitle)
        }

        composable("add_special_reminder") { AddReminderScreen("Special Day Reminder") }
        composable("add_event_reminder") { AddReminderScreen("Event Reminder") }

        //Adding memories to the firebase
        composable("add_memory") {
            AddMemoryScreen(navController, memoryViewModel)
        }

    }
}
