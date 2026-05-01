package com.example.lifetrack.ui.screens.editors

import com.example.lifetrack.viewModel.MemoryViewModel
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lifetrack.R
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemoryScreen(navController: NavController, viewModel: MemoryViewModel, memoryId: String? = null) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") } // Standard: yyyy-MM-dd
    var time by remember { mutableStateOf("") } // Standard: HH:mm
    
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedVideos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedImages = (selectedImages + uris).distinct()
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedVideos = (selectedVideos + uris).distinct()
    }

    // Load memory if editing
    LaunchedEffect(memoryId) {
        if (memoryId != null) {
            viewModel.getMemoryById(memoryId) { memory ->
                if (memory != null) {
                    title = memory.title
                    description = memory.description
                    date = memory.date
                    time = memory.time
                    selectedImages = memory.imageUrls.map { Uri.parse(it) }
                    selectedVideos = memory.videoUrls.map { Uri.parse(it) }
                }
            }
        }
    }

    LaunchedEffect(viewModel.isSuccess.value) {
        if (viewModel.isSuccess.value) {
            Toast.makeText(context, viewModel.message.value, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        date = DateTimeUtils.getStorageDate(selectedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = DarkGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = DarkGreen)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    time = DateTimeUtils.getStorageTime(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK", color = DarkGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = DarkGreen)
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.life_track_logo_transperant),
                            contentDescription = "Logo",
                            modifier = Modifier.size(60.dp).clip(shape = RoundedCornerShape(15.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (memoryId == null) "Add Memory" else "Edit Memory",
                            fontWeight = FontWeight.Bold, 
                            color = DarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = white)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(white)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Preserve your special moments",
                fontSize = 14.sp,
                color = GreenLime,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = if (date.isNotEmpty()) DateTimeUtils.formatForDisplay(date) else "",
                    onValueChange = { },
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, null, tint = DarkGreen)
                        }
                    },
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = if (time.isNotEmpty()) DateTimeUtils.formatTimeForDisplay(time) else "",
                    onValueChange = { },
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.Schedule, null, tint = DarkGreen)
                        }
                    },
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            MediaUploadSection(
                title = "Capture Images",
                uris = selectedImages,
                onAddClick = { imageLauncher.launch("image/*") },
                onRemoveClick = { uri -> selectedImages = selectedImages.filter { it != uri } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MediaUploadSection(
                title = "Capture Videos",
                uris = selectedVideos,
                onAddClick = { videoLauncher.launch("video/*") },
                onRemoveClick = { uri -> selectedVideos = selectedVideos.filter { it != uri } }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && userId.isNotEmpty()) {
                        viewModel.saveMemory(
                            context = context,
                            title = title,
                            description = description,
                            date = date,
                            time = time,
                            images = selectedImages,
                            videos = selectedVideos,
                            userId = userId,
                            id = memoryId ?: ""
                        )
                    } else {
                        Toast.makeText(context, "Title, Date and Time are required", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                enabled = !viewModel.isSaving.value
            ) {
                if (viewModel.isSaving.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        text = if (memoryId == null) "Save Memory" else "Update Memory",
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MediaUploadSection(
    title: String,
    uris: List<Uri>,
    onAddClick: () -> Unit,
    onRemoveClick: (Uri) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.SemiBold, color = DarkGreen)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Box(
                    modifier = Modifier.size(80.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = DarkGreen)
                }
            }
            items(uris) { uri ->
                Box(modifier = Modifier.size(80.dp)) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onRemoveClick(uri) },
                        modifier = Modifier.size(24.dp).align(Alignment.TopEnd).background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
