import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lifetrack.R
import com.example.lifetrack.navigation.BottomNavItem.Home.icon
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.Green
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.google.common.base.Defaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab() {
    // User State
    var name by remember { mutableStateOf("Md. Asif Samrat") }
    var age by remember { mutableStateOf("25") }
    var occupation by remember { mutableStateOf("Software Engineer") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    var showEditDialog by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }


    //Setting the custom time for showing the remaining time
    val calendar = java.util.Calendar.getInstance()
    val upcomingReminders = listOf(
        ReminderItem("Meeting with Team", calendar.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 20)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }.timeInMillis, "Oct 27th 25"),

        ReminderItem("Doctor Appointment", calendar.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 14)
            set(java.util.Calendar.MINUTE, 30)
            set(java.util.Calendar.SECOND, 0)
        }.timeInMillis, "Oct 28th 25"),

        ReminderItem("Grocery Shopping", calendar.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 18)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }.timeInMillis, "Oct 29th 25")
    )

    val upcomingMemories = listOf(
        ReminderItem("Family Picnic", 0L, "Apr 2nd 26"),
        ReminderItem("Graduation Day", 0L, "May 15th 26"),
        ReminderItem("First Job Anniversary", 0L, "June 10th 26")
    )



    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(GreenLight, GreenLime, Green)
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.life_track_logo_text_transperant),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(100.dp)
            )
        }

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Profile Section
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
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { imageLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }

                // Personal Info
                CompositionLocalProvider(LocalContentColor provides DarkGreen) {

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.offset(y = 20.dp)
                            //.background(DarkGreen)
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
            }

            //Vertical Divider
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
                    .shadow(2.5.dp, RoundedCornerShape(10.dp))
            )

            // Upcomming reminder and memeories
            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 10.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Reminder",
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Upcoming Reminders",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }


                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    upcomingReminders.take(3).forEach {
                        ReminderCard(it)
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Memories",
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Upcoming Memories",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    upcomingMemories.take(3).forEach {
                        MemoryCard(it)
                    }
                }
            }
        }
    }



    // Edit Info Dialog
    if (showEditDialog) {
        var tempName by remember { mutableStateOf(name) }
        var tempAge by remember { mutableStateOf(age) }
        var tempOccupation by remember { mutableStateOf(occupation) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
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
                    name = tempName
                    age = tempAge
                    occupation = tempOccupation
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReminderCard(reminder: ReminderItem) {
    val remainingTime by rememberCountdown(reminder.deadlineMillis)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Remaining: ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = remainingTime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenLime
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .alpha(alpha)
                    .background(Color.Red, CircleShape)
            )
        }
    }
}

@Composable
fun MemoryCard(reminder: ReminderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Date: ${reminder.date}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenLime
                )
                Text(
                    text = "A beautiful memory captured",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                tint = GreenLight,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

fun getRemainingTime(deadlineMillis: Long): String {
    val currentMillis = System.currentTimeMillis()
    var diff = deadlineMillis - currentMillis

    if (diff <= 0) return "Time's up"

    val seconds = diff / 1000 % 60
    val minutes = diff / (1000 * 60) % 60
    val hours = diff / (1000 * 60 * 60) % 24
    val days = diff / (1000 * 60 * 60 * 24)

    return "${days}d ${hours}h ${minutes}m ${seconds}s"
}

@Composable
fun rememberCountdown(deadlineMillis: Long): State<String> {
    val time = remember { mutableStateOf(getRemainingTime(deadlineMillis)) }

    LaunchedEffect(deadlineMillis) {
        while (true) {
            time.value = getRemainingTime(deadlineMillis)
            kotlinx.coroutines.delay(1000L)
        }
    }

    return time
}

//data class ReminderItem(val title: String, val time: String)
data class ReminderItem(
    val title: String,
    val deadlineMillis: Long,
    val date: String = ""
)