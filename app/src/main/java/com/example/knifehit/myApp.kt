package com.example.knifehit
import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start the music when the application starts
        MusicManager.start(this, R.raw.music)
    }
}
