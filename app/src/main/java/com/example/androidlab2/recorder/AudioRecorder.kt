package com.example.androidlab2.recorder

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    var outputFile: String = ""
        private set

    fun startRecording(): Boolean {
        return try {
            val dir = File(context.getExternalFilesDir(null), "Recordings")
            if (!dir.exists()) dir.mkdirs()
            val currentTime = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            outputFile = "${dir.absolutePath}/recording_$currentTime.3gp"

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile)
                prepare()
                start()
            }
            true
        } catch (e: IOException) {
            Log.e("AudioRecorder", "녹음 실패", e)
            false
        }
    }

    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            File(outputFile)
        } catch (e: Exception) {
            Log.e("AudioRecorder", "녹음 중지 실패", e)
            null
        }
    }
}