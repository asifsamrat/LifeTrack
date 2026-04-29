import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lifetrack.ui.theme.DarkGreen

@Composable
fun ProfileInfo() {
    // Profile Section
    var name by remember { mutableStateOf("Md. Asif Samrat") }
    var age by remember { mutableStateOf("25") }
    var occupation by remember { mutableStateOf("Software Engineer") }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-40).dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left: Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .shadow(10.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

            }

            // Personal Info
            CompositionLocalProvider(LocalContentColor provides DarkGreen) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.offset(y = 20.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Age: $age",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Occupation: $occupation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }


            //Edit Indicator
            Box(
                modifier = Modifier
                    .offset(y = (-12).dp)
                    .size(32.dp)
                    .background(
                        Color.White, CircleShape
                    )
                    .clickable {
                        showEditDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = DarkGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            //Opening the Edit Dialog box When the click edit icon
            if (showEditDialog) {
                ShowEditDialog(
                    name = name,
                    age = age,
                    occupation = occupation,
                    onSave = { newName, newAge, newOccupation ->
                        name = newName
                        age = newAge
                        occupation = newOccupation
                        showEditDialog = false
                    },
                    onDismiss = {
                        showEditDialog = false
                    }
                )
            }
        }
    }
}


@Composable
fun ShowEditDialog(
    name: String,
    age: String,
    occupation: String,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempName by remember { mutableStateOf(name) }
    var tempAge by remember { mutableStateOf(age) }
    var tempOccupation by remember { mutableStateOf(occupation) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Name") })
                OutlinedTextField(value = tempAge, onValueChange = { tempAge = it }, label = { Text("Age") })
                OutlinedTextField(value = tempOccupation, onValueChange = { tempOccupation = it }, label = { Text("Occupation") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(tempName, tempAge, tempOccupation)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}