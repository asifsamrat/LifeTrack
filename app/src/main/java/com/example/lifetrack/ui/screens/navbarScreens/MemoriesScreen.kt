package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.R
import com.example.lifetrack.ui.components.cards.MemoriesCard
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.MemoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(navController: NavController, memoryViewModel: MemoryViewModel) {
    
    // Use cached memories from ViewModel
    val allMemories by memoryViewModel.memories
    
    // Sort memories locally
    val sortedMemories = remember(allMemories) {
        allMemories.sortedByDescending { DateTimeUtils.parseToMillis(it.date, it.time) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.life_track_logo_transperant),
                            contentDescription = "Logo",
                            modifier = Modifier.size(60.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "My Memories",
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = white
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .background(color = white)
        ) {
            if (sortedMemories.isEmpty()) {
                Text(
                    text = "No memories saved yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sortedMemories) { memory ->
                        MemoriesCard(
                            memory = memory,
                            onEdit = {
                                navController.navigate("add_memory?memoryId=${it.id}")
                            },
                            onDelete = {
                                memoryViewModel.deleteMemory(it.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
