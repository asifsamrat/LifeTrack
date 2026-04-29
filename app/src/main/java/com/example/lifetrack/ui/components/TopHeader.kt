package com.example.lifetrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lifetrack.R
import com.example.lifetrack.ui.theme.Green
import com.example.lifetrack.ui.theme.GreenLight
import com.example.lifetrack.ui.theme.GreenLime

@Composable
fun TopHeader() {
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
}