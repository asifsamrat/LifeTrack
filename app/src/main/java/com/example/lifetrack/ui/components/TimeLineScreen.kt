import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifetrack.data.model.Note
import com.example.lifetrack.ui.theme.DarkGreen
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun TimeLineScreen() {
    //Setting the custom time for showing the remaining time
    val calendar = Calendar.getInstance()
    val upcomingReminders = listOf(
        ReminderItem("Meeting with Team", calendar.apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis, "Oct 27th 25"),

        ReminderItem("Doctor Appointment", calendar.apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }.timeInMillis, "Oct 28th 25"),

        ReminderItem("Grocery Shopping", calendar.apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis, "Oct 29th 25")
    )

    val upcomingMemories = listOf(
        ReminderItem("Family Picnic", 0L, "Apr 2nd 26"),
        ReminderItem("Graduation Day", 0L, "May 15th 26"),
        ReminderItem("First Job Anniversary", 0L, "June 10th 26")
    )

    val recentNotes = listOf(
        Note("Daily Note", "This is the daily note", "This is the daily note This is the daily note", "Oct 27th 25"),
        Note("Special Note", "This is the daily note", "This is the daily note This is the daily note", "Oct 27th 25"),
        Note("Daily Note", "This is the daily note", "This is the daily note This is the daily note", "Oct 27th 25")
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
                TimeLineMemoriesCard(it)
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Note",
                tint = DarkGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Recent Notes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            recentNotes.take(3).forEach {
                NoteCard(it)
            }
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
            delay(1000L)
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