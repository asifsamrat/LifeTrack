package com.example.lifetrack.ui.screens.editors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifetrack.R
import com.example.lifetrack.data.model.Reminder
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.viewModel.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(initialTitle: String = "", navController: NavController, viewModel: ReminderViewModel) {

    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reminderType by remember { mutableStateOf(if (initialTitle.contains("Special")) "Special" else "Event") }
    var indicatorColor by remember { mutableStateOf("Green") }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    var typeExpanded by remember { mutableStateOf(false) }
    var colorExpanded by remember { mutableStateOf(false) }

    val types = listOf("Event", "Special")
    val colors = listOf("Red", "Green", "Yellow", "Blue")

    // React to ViewModel state changes
    LaunchedEffect(viewModel.isSuccess.value) {
        if (viewModel.isSuccess.value) {
            android.widget.Toast.makeText(context, viewModel.message.value, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate))
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

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
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
                            text = "Add Reminder",
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Set a new reminder for your events",
                fontSize = 14.sp,
                color = GreenLime,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp)
            )

            // Reminder Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = reminderType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reminder Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen)
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                reminderType = type
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            // Date Selection
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
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

            // Time Selection
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time") },
                placeholder = { Text("Select Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Select Time",
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

            if (reminderType == "Event") {
                Spacer(modifier = Modifier.height(16.dp))
                // Indicator Color Dropdown
                ExposedDropdownMenuBox(
                    expanded = colorExpanded,
                    onExpandedChange = { colorExpanded = !colorExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = indicatorColor,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Indicator Color") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, focusedLabelColor = DarkGreen)
                    )
                    ExposedDropdownMenu(
                        expanded = colorExpanded,
                        onDismissRequest = { colorExpanded = false }
                    ) {
                        colors.forEach { color ->
                            DropdownMenuItem(
                                text = { Text(color) },
                                onClick = {
                                    indicatorColor = color
                                    colorExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && date.isNotEmpty() && userId.isNotEmpty()) {
                        val reminder = Reminder(
                            reminderType = reminderType,
                            title = title,
                            date = date,
                            time = time,
                            userId = userId,
                            indicatorColor = indicatorColor
                        )
                        viewModel.saveReminder(reminder)
                    } else {
                        android.widget.Toast.makeText(context, "Please fill required fields", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen
                ),
                enabled = !viewModel.isSaving.value
            ) {
                if (viewModel.isSaving.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        "Save Reminder",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
