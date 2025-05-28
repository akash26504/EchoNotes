package com.example.videosummarise

import android.app.Application
import com.example.videosummarise.utils.ThemeManager

class VideoSummariseApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize theme on app startup
        ThemeManager.initializeTheme(this)
    }
}
