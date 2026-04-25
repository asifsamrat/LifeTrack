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
fun AddNoteScreen() {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Add Note", style = MaterialTheme.typography.titleLarge)

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )

        Button(onClick = {
            // TODO: Save to Firebase
        }) {
            Text("Save")
        }
    }
}