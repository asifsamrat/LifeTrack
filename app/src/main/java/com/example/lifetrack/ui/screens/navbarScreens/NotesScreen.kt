package com.example.lifetrack.ui.screens.navbarScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.lifetrack.ui.components.cards.NoteCard
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.NoteViewModel
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
                // Sorting notes by date (newest first)
                noteList = notes.sortedByDescending { DateTimeUtils.parseToMillis(it.date) }
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
                if (noteList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No notes found", color = Color.Gray)
                        }
                    }
                } else {
                    items(noteList) { note ->
                        NoteCard(
                            note = note,
                            onEdit = {
                                rootNavController.navigate("add_note?noteType=${note.noteType}&noteId=${note.id}")
                            },
                            onDelete = {
                                noteViewModel.deleteNote(note.id) { success ->
                                    if (success) {
                                        noteList = noteList.filter { it.id != note.id }
                                    }
                                }
                            }
                        )
                    }
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
                color = if (isSelected) DarkGreen else GreenLight,
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
