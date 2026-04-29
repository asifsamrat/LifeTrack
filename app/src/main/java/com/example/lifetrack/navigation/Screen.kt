package com.example.lifetrack.navigation
import com.example.lifetrack.viewModel.NoteViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifetrack.ui.screens.navbarScreens.MemoriesScreen
import com.example.lifetrack.ui.screens.navbarScreens.NotesScreen
import com.example.lifetrack.ui.screens.navbarScreens.ReminderScreen

@Composable
fun HomeScreen(rootNavController: NavController) {

    val bottomNavController = rememberNavController()

    NavHost(
        navController = bottomNavController,
        startDestination = BottomNavItem.Home.route
    ) {

        composable(BottomNavItem.Home.route) {
            HomeScreen(rootNavController)
        }

        composable(BottomNavItem.Notes.route) {
            val noteViewModel: NoteViewModel = viewModel()
            NotesScreen(rootNavController, noteViewModel)
        }

//        composable(BottomNavItem.Reminder.route) {
//            ReminderScreen(rootNavController)
//        }
//
//        composable(BottomNavItem.Memories.route) {
//            MemoriesScreen(rootNavController)
//        }
    }
}
