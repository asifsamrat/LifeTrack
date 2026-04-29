package com.example.lifetrack

import android.app.Application
import com.cloudinary.android.MediaManager

class LifeTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "dglqws4l4", // Replace with your cloud name
            "api_key" to "your_api_key",       // Replace with your api key
            "api_secret" to "your_api_secret"  // Replace with your api secret
        )
        MediaManager.init(this, config)
    }
}
