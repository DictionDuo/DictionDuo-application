package com.example.androidlab2.util

import com.example.androidlab2.model.RecordingItem
import java.io.File

object RecordingFileManager {

    fun loadRecordings(dir: File): List<RecordingItem> {
        return dir.listFiles { _, name -> name.endsWith(".3gp") }?.map {
            RecordingItem(it.name, it.lastModified(), it)
        } ?: emptyList()
    }

    fun rename(item: RecordingItem, newName: String): RecordingItem? {
        val newFile = File(item.file.parent, newName)
        return if (item.file.renameTo(newFile)) {
            item.copy(name = newName, file = newFile)
        } else null
    }

    fun delete(item: RecordingItem): Boolean {
        return item.file.exists() && item.file.delete()
    }
}
