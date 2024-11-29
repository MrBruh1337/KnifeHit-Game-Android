package com.example.knifehit

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    var isMusicMuted = false
    fun start(context: Context, resourceId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, resourceId)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            mediaPlayer?.setVolume(0.2f,0.2f)
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
    fun pause(){
        mediaPlayer?.pause()
    }
    fun resume(){
        mediaPlayer?.start()
    }
    fun toggleMute() {
        if (isMusicMuted) {
            mediaPlayer?.setVolume(0.2f,0.2f)
        } else {
            mediaPlayer?.setVolume(0.0f,0.0f)
        }
        isMusicMuted = !isMusicMuted
    }

    // Check if the music is muted
    fun isMuted(): Boolean {
        return isMusicMuted
    }
}
