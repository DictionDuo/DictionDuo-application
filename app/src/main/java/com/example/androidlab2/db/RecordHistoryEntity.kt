package com.example.androidlab2.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_history")
data class RecordHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val time: String,
    val testResult: String,
    val filePath: String
)