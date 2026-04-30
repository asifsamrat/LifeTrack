package com.example.lifetrack.ui.screens.editors

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.R
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils
import com.example.lifetrack.viewModel.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    initialReminderType: String,
    navController: NavController,
    viewModel: ReminderViewModel
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") } // Stores yyyy-MM-dd
    var time by remember { mutableStateOf("") } // Stores HH:mm
    
    var remindDays by remember { mutableStateOf("0") }
    var remindHours by remember { mutableStateOf("0") }
    var remindMinutes by remember { mutableStateOf("0") }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val message by viewModel.message
    val success by viewModel.isSuccess

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    // Determine the type for display and storage
    val displayType = if (initialReminderType.contains("Special")) "Special Day" else "Event"
    val storedReminderType = if (initialReminderType.contains("Special")) "Special" else "Event"

    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                        Image(
                            painter = painterResource(id = R.drawable.life_track_logo_transperant),
                            contentDescription = "Logo",
                            modifier = Modifier.size(60.dp).clip(shape = RoundedCornerShape(15.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Add New $displayType", fontWeight = FontWeight.Bold, color = DarkGreen)
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Set your reminder details",
                fontSize = 14.sp,
                color = GreenLime,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp)
            )

            // Title Field
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

            // Date Selection Field
            OutlinedTextField(
                value = if (date.isNotEmpty()) DateTimeUtils.formatForDisplay(date) else "",
                onValueChange = { },
                label = { Text("Date") },
                placeholder = { Text("Select Date") },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = DarkGreen)
                    }
                },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Selection Field
            OutlinedTextField(
                value = if (time.isNotEmpty()) DateTimeUtils.formatTimeForDisplay(time) else "",
                onValueChange = { },
                label = { Text("Time") },
                placeholder = { Text("Select Time") },
                modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = DarkGreen)
                    }
                },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Remind Me Before Section
            Text(
                text = "Remind me before",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = remindDays,
                    onValueChange = { if (it.all { char -> char.isDigit() }) remindDays = it },
                    label = { Text("Days") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = remindHours,
                    onValueChange = { if (it.all { char -> char.isDigit() }) remindHours = it },
                    label = { Text("Hours") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = remindMinutes,
                    onValueChange = { if (it.all { char -> char.isDigit() }) remindMinutes = it },
                    label = { Text("Mins") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && userId.isNotEmpty()) {
                        val reminder = Reminder(
                            reminderType = storedReminderType,
                            title = title,
                            date = date,
                            time = time,
                            userId = userId,
                            remindDays = remindDays.toIntOrNull() ?: 0,
                            remindHours = remindHours.toIntOrNull() ?: 0,
                            remindMinutes = remindMinutes.toIntOrNull() ?: 0,
                            indicatorColor = "Green"
                        )
                        viewModel.saveReminder(reminder)
                    } else {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
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
                    Text("Save Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
