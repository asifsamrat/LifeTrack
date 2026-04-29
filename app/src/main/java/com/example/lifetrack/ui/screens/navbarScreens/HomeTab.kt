package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.ui.components.ProfileInfo
import com.example.lifetrack.ui.components.TimeLineScreen
import com.example.lifetrack.ui.components.TopHeader
import com.example.lifetrack.viewModel.NotificationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(navController: NavController, notificationViewModel: NotificationViewModel) {
    val unreadCount = notificationViewModel.unreadCount

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Header Section
        TopHeader(
            unreadCount = unreadCount,
            onNotificationClick = {
                navController.navigate("notification")
            }
        )

        // Profile Section
        ProfileInfo()

        // Upcomming reminder and memeories Time line
        TimeLineScreen()
    }
}
