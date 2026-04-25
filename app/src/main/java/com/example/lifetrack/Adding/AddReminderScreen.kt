package com.example.lifetrack.Adding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddReminderScreen(initialTitle: String = "") {

    var title by remember { mutableStateOf(initialTitle) }
    var dateTime by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Add Reminder", style = MaterialTheme.typography.titleLarge)

        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })

        TextField(
            value = dateTime,
            onValueChange = { dateTime = it },
            label = { Text("Date & Time") }
        )

        Button(onClick = {
            // TODO: Save Reminder
        }) {
            Text("Save")
        }
    }
}