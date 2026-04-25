package com.example.lifetrack.navigation

sealed class Screen (val route: String){
    object Login : Screen("login")
    object Register : Screen("register")
    object Forgot : Screen("forgot")
    object HomeMain : Screen("home_main")

    // Add notes button
    object AddNote : Screen("add_note")
    object AddSpecialNote : Screen("add_special_note")
    object AddSpecialReminder : Screen("add_special_reminder")
    object AddEventReminder : Screen("add_event_reminder")
    object AddMemory : Screen("add_memory")
}