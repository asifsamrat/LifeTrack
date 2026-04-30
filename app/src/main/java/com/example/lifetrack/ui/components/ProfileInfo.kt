package com.example.lifetrack.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.viewModel.ProfileViewModel
import org.tensorflow.lite.schema.Padding

@Composable
fun ProfileInfo(profileViewModel: ProfileViewModel = viewModel()) {
    val userProfile by profileViewModel.userProfile
    val isLoading by profileViewModel.isLoading
    val message by profileViewModel.message
    
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Launcher to pick a profile image when the profile icon is clicked
    val profileImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.updateProfileImage(context, it)
        }
    }

    Column(
        modifier = Modifier.padding(
            start = 8.dp,
            top = 8.dp,
            end = 8.dp,
            bottom = 0.dp
        ).height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-40).dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left: Profile Image - Clickable to update image directly
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .shadow(10.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        profileImageLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile?.profileImageUrl.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    AsyncImage(
                        model = userProfile?.profileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = DarkGreen,
                        strokeWidth = 4.dp
                    )
                }
            }

            // Personal Info
            CompositionLocalProvider(LocalContentColor provides DarkGreen) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.offset(y = 20.dp).weight(1f).padding(start = 16.dp)
                ) {
                    Text(
                        text = userProfile?.name ?: "No Name",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Age: ${userProfile?.age ?: "N/A"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Occupation: ${userProfile?.occupation ?: "N/A"}",
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
                    initialName = userProfile?.name ?: "",
                    initialAge = userProfile?.age ?: "",
                    initialOccupation = userProfile?.occupation ?: "",
                    onSave = { newName, newAge, newOccupation, imageUri ->
                        profileViewModel.updateProfile(context, newName, newAge, newOccupation, imageUri)
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
    initialName: String,
    initialAge: String,
    initialOccupation: String,
    onSave: (String, String, String, Uri?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempName by remember { mutableStateOf(initialName) }
    var tempAge by remember { mutableStateOf(initialAge) }
    var tempOccupation by remember { mutableStateOf(initialOccupation) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedImageUri == null) "Select Profile Image" else "Image Selected")
                }
                
                OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Name") })
                OutlinedTextField(value = tempAge, onValueChange = { tempAge = it }, label = { Text("Age") })
                OutlinedTextField(value = tempOccupation, onValueChange = { tempOccupation = it }, label = { Text("Occupation") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(tempName, tempAge, tempOccupation, selectedImageUri)
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
