package com.example.lifetrack.Adding

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.lifetrack.DataModel.Memory
import com.example.lifetrack.Saves.saveMemory
import com.example.lifetrack.Uploads.MemoryFiles
import java.util.Calendar

@Composable
fun AddMemoryScreen() {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var viewerImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var viewerVideoUri by remember { mutableStateOf<Uri?>(null) }

    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val datePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> date = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute -> time = String.format("%02d:%02d", hourOfDay, minute) },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedImages = (selectedImages + uris).distinct()
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedVideos = (selectedVideos + uris).distinct()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Add Memory", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = date, onValueChange = {}, readOnly = true, label = { Text("Date") }, modifier = Modifier.weight(1f), trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) { Icon(Icons.Default.CalendarMonth, null) }
                })
                OutlinedTextField(value = time, onValueChange = {}, readOnly = true, label = { Text("Time") }, modifier = Modifier.weight(1f), trailingIcon = {
                    IconButton(onClick = { timePicker.show() }) { Icon(Icons.Default.Schedule, null) }
                })
            }

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            
            OutlinedTextField(
                value = description, 
                onValueChange = { description = it }, 
                label = { Text("Description") }, 
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Sections
            UploadRow("Images", selectedImages, { imageLauncher.launch("image/*") }, { selectedImages = selectedImages.filter { it != it } }) { viewerImageUri = it }
            UploadRow("Videos", selectedVideos, { videoLauncher.launch("video/*") }, { selectedVideos = selectedVideos.filter { it != it } }) { viewerVideoUri = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isEmpty() || userId.isEmpty()) {
                        Toast.makeText(context, "Please enter title and login", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    isSaving = true
                    // Step 1: upload images
                    MemoryFiles(selectedImages, "images") { imageUrls ->
                        // Step 2: upload videos
                        MemoryFiles(selectedVideos, "videos") { videoUrls ->
                            val memory = Memory(
                                title = title,
                                description = description,
                                date = date,
                                time = time,
                                imageUrls = imageUrls,
                                videoUrls = videoUrls,
                                userId = userId
                            )
                            saveMemory(memory) { success ->
                                isSaving = false
                                if (success) {
                                    Toast.makeText(context, "Memory Saved!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save to database", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
//                if (isSaving) CircularProgressIndicator(size = 20.dp, color = Color.White)
//                else Text("Save Memory")
            }
        }
    }
}

@Composable
fun UploadRow(label: String, uris: List<Uri>, onAdd: () -> Unit, onRemove: (Uri) -> Unit, onView: (Uri) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onAdd, modifier = Modifier.weight(1f)) { Text("Add $label") }
        LazyRow(modifier = Modifier.weight(2f)) {
            items(uris) { uri ->
                Box(modifier = Modifier.size(50.dp).padding(2.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray).clickable { onView(uri) }) {
                    AsyncImage(model = uri, contentDescription = null, contentScale = ContentScale.Crop)
                }
            }
        }
    }
}
