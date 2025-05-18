package com.example.androidlab2.audio

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import java.io.File

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun play(file: File) {
        release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(file.absolutePath)
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "오디오 재생 실패", Toast.LENGTH_SHORT).show()
            }
        }

        mediaPlayer?.setOnCompletionListener {
            release()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
