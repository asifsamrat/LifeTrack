package com.example.lifetrack.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifetrack.data.model.Memory
import com.example.lifetrack.ui.theme.DarkGreen
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.GreenLime
import com.example.lifetrack.ui.theme.white
import com.example.lifetrack.utils.DateTimeUtils

@Composable
fun TimeLineMemoriesCard(memory: Memory) {
    val nextOccur = remember(memory) { DateTimeUtils.getNextOccurrence(memory.date, memory.time) }
    val displayDate = remember(nextOccur) { DateTimeUtils.getStorageDate(nextOccur) }

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
                    text = memory.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Next Anniversary: ${DateTimeUtils.formatForDisplay(displayDate)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenLime
                )

                Text(
                    text = "A beautiful memory repeating yearly",
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
