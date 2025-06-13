package com.example.androidlab2.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RecordHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecordHistoryDatabase : RoomDatabase() {
    abstract fun recordHistoryDao(): RecordHistoryDao
}