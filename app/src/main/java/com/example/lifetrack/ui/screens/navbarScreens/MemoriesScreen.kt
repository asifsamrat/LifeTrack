package com.example.lifetrack.ui.screens.navbarScreens

import ExpandableText
import MemoriesCard
import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.R
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.white
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    
    var memories by remember { mutableStateOf<List<Memory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("memories")
                .whereEqualTo("userId", userId)
                // Removed .orderBy("date") to avoid Index error
                .addSnapshotListener { snapshot, error ->
                    isLoading = false
                    if (error != null) {
                        errorMessage = error.message
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        // Sort in memory instead of server-side to fix the Index error
                        memories = snapshot.toObjects(Memory::class.java)
                            .sortedByDescending { it.date }
                    }
                }
        } else {
            isLoading = false
            errorMessage = "User not logged in"
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (memories.isEmpty()) {
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
                    items(memories) { memory ->
                        MemoriesCard(memory)
                    }
                }
            }
        }
    }
}
