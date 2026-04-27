package com.example.lifetrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lifetrack.Adding.AddMemoryScreen
import com.example.lifetrack.Adding.AddNoteScreen
import com.example.lifetrack.Adding.AddReminderScreen
import com.example.lifetrack.ui.screens.authenticationScreen.ForgotPasswordScreen
import com.example.lifetrack.ui.screens.HomeScreen
import com.example.lifetrack.ui.screens.authenticationScreen.LoginScreen
import com.example.lifetrack.ui.screens.authenticationScreen.RegisterScreen

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Forgot.route) {
            ForgotPasswordScreen(navController)
        }

        composable(Screen.HomeMain.route) {
            HomeScreen(rootNavController = navController)
        }

        // Add notes button
        composable("add_note") { AddNoteScreen() }
        composable("add_special_note") { AddNoteScreen() }
        
        composable(
            route = "add_reminder/{initialTitle}",
            arguments = listOf(navArgument("initialTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val initialTitle = backStackEntry.arguments?.getString("initialTitle") ?: ""
            AddReminderScreen(initialTitle)
        }

        composable("add_special_reminder") { AddReminderScreen("Special Day Reminder") }
        composable("add_event_reminder") { AddReminderScreen("Event Reminder") }

        composable("add_memory") { AddMemoryScreen() }

    }
}
