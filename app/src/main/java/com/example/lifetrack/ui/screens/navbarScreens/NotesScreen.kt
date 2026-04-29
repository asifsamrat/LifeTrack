package com.example.lifetrack.ui.screens.navbarScreens

import NoteCard
import NoteViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.R
import com.example.lifetrack.data.model.Note
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(rootNavController: NavController, noteViewModel: NoteViewModel) {
    var selectedTab by remember { mutableStateOf("Daily Note") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    var noteList by remember { mutableStateOf<List<Note>>(emptyList()) }

    LaunchedEffect(selectedTab, userId) {
        if (userId.isNotEmpty()) {
            noteViewModel.getNotesByType(userId, selectedTab) { notes ->
                noteList = notes
            }
        }
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
                            text = "My Notes",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(white)
        ) {
            // Tab Selection Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                TabButton(
                    text = "Daily Notes",
                    isSelected = selectedTab == "Daily Note",
                    onClick = { selectedTab = "Daily Note" },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Special Notes",
                    isSelected = selectedTab == "Special Note",
                    onClick = { selectedTab = "Special Note" },
                    modifier = Modifier.weight(1f)
                )
            }

            // Notes List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(noteList) { note ->
                    NoteCard(note)
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = if (isSelected) DarkGreen else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}
