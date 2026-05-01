package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.ui.components.ProfileInfo
import com.example.lifetrack.ui.components.TimeLineScreen
import com.example.lifetrack.ui.components.TopHeader
import com.example.lifetrack.viewModel.MemoryViewModel
import com.example.lifetrack.viewModel.NoteViewModel
import com.example.lifetrack.viewModel.NotificationViewModel
import com.example.lifetrack.viewModel.ReminderViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    navController: NavController, 
    notificationViewModel: NotificationViewModel,
    noteViewModel: NoteViewModel,
    reminderViewModel: ReminderViewModel,
    memoryViewModel: MemoryViewModel
) {
    val unreadCount = notificationViewModel.unreadCount

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            Column() {
                // Top Header Section
                TopHeader(
                    unreadCount = unreadCount,
                    onNotificationClick = {
                        navController.navigate("notification")
                    }
                )

                // Profile Section
                ProfileInfo()

            }
        }
        // Upcoming reminder and memories Time line
        TimeLineScreen(
            noteViewModel = noteViewModel,
            reminderViewModel = reminderViewModel,
            memoryViewModel = memoryViewModel
        )
    }
}
