package com.example.lifetrack.ui.components.cards

import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.utils.ExpandableText

@Composable
fun MemoriesCard(
    memory: Memory,
    onEdit: (Memory) -> Unit,
    onDelete: (Memory) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = white,
            contentColor = DarkGreen
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${DateTimeUtils.formatForDisplay(memory.date)} at ${DateTimeUtils.formatTimeForDisplay(memory.time)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(end = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
                
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = DarkGreen)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(
                            color = white,
                        )
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = DarkGreen) },
                            onClick = {
                                showMenu = false
                                onEdit(memory)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDelete(memory)
                            }
                        )
                    }
                }
            }

            // Videos Section
            if (memory.videoUrls.isNotEmpty()) {
                Text(
                    text = "Videos",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(memory.videoUrls) { videoUrl ->
                        VideoCard(videoUrl)
                    }
                }
            }

            // Images Section
            if (memory.imageUrls.isNotEmpty()) {
                Text(
                    text = "Images",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(95.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(memory.imageUrls) { imageUrl ->
                        ImageCard(imageUrl)
                    }
                }
            }

            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            
            Text(
                text = memory.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(8.dp))
            ExpandableText(memory.description)
        }
    }
}

@Composable
fun VideoCard(videoUrl: String) {
    var isBuffering by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(75.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (hasError) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = Color.Red)
                    Text("Error loading video", fontSize = 10.sp, color = Color.Red)
                }
            } else {
                AndroidView(
                    factory = { context ->
                        VideoView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            
                            setVideoURI(Uri.parse(videoUrl))
                            
                            setOnPreparedListener { mp ->
                                isBuffering = false
                                mp.isLooping = true
                                // Start muted for preview
                                mp.setVolume(0f, 0f) 
                                start()
                            }
                            
                            setOnErrorListener { _, _, _ ->
                                isBuffering = false
                                hasError = true
                                true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            if (isBuffering && !hasError) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = DarkGreen,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
fun ImageCard(imageUrl: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(75.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Memory Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
