package com.example.androidlab2.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
    const val RECORD_AUDIO_PERMISSION_CODE = 100
    const val WRITE_STORAGE_PERMISSION_CODE = 101

    fun checkPermissions(activity: Activity, instructionView: TextView) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_CODE)
        } else {
            instructionView.text = "마이크 버튼을 클릭하여 발음을 녹음하세요."
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_STORAGE_PERMISSION_CODE)
        }
    }
}