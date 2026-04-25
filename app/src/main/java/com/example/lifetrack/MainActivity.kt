package com.example.lifetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lifetrack.navigation.AppNavigator
import com.example.lifetrack.ui.theme.LifeTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeTrackTheme {
                AppNavigator()
            }
        }
    }
}
