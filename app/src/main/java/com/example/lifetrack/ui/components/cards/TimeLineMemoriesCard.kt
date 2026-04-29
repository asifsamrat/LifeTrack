import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white

@Composable
fun TimeLineMemoriesCard(reminder: ReminderItem) {
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