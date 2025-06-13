package com.example.androidlab2.model

import java.io.File

data class RecordingItem (
    val name: String,
    val date: Long,
    val file: File,
    val result: String? = null
)