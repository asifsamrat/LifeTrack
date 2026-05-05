package com.example.lifetrack.ui.screens.editors

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lifetrack.R
import com.example.lifetrack.data.model.Note
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.NoteViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteType: String,
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    noteId: String? = null
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") } // Stores yyyy-MM-dd
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    //Note save message and success
    val message by noteViewModel.message
    val success by noteViewModel.isSuccess
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // For Button interaction and changing color
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonBgColor = if (isPressed) GreenLime else DarkGreen
    val buttonTextColor = if (isPressed) Color.White else Color.White

    // Load existing note if editing
    LaunchedEffect(noteId) {
        if (noteId != null) {
            noteViewModel.getNoteById(noteId) { note ->
                if (note != null) {
                    title = note.title
                    description = note.description
                    date = note.date
                }
            }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.life_track_logo_transperant),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (noteId == null) "Add New $noteType" else "Edit $noteType",
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                text = "Capture your thoughts",
                fontSize = 14.sp,
                color = GreenLime,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkGreen,
                    focusedLabelColor = DarkGreen,
                    cursorColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (date.isNotEmpty()) DateTimeUtils.formatForDisplay(date) else "",
                onValueChange = { },
                label = { Text("Date") },
                placeholder = { Text("Select Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Date",
                            tint = DarkGreen
                        )
                    }
                },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkGreen,
                    focusedLabelColor = DarkGreen,
                    cursorColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkGreen,
                    focusedLabelColor = DarkGreen,
                    cursorColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            LaunchedEffect(success) {
                if (success) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    noteViewModel.resetState()
                    navController.popBackStack()
                }
            }

            if (message != "" && !success) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " *",
                        color = Color.Red
                    )

                    Text(
                        text = message,
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                }
            }

            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank() || date.isBlank()) {
                        noteViewModel.message.value = "Please fill in all fields."
                        return@Button
                    }
                    val note = Note(
                        id = noteId ?: "",
                        noteType = noteType,
                        title = title,
                        description = description,
                        date = date,
                        userId = userId
                    )
                    noteViewModel.saveOrupdateNote(note)
                },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBgColor
                )
            ) {
                Text(
                    text = if (noteId == null) "Save Note" else "Update Note",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = buttonTextColor
                )
            }
        }
    }
}
