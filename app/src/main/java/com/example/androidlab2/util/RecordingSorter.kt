package com.example.androidlab2.util

import com.example.androidlab2.model.RecordingItem

object RecordingSorter {
    fun sortByTime(list: MutableList<RecordingItem>, ascending: Boolean) {
        if (ascending) list.sortBy { it.date } else list.sortByDescending { it.date }
    }

    fun sortByName(list: MutableList<RecordingItem>, ascending: Boolean) {
        if (ascending) list.sortBy { it.name } else list.sortByDescending { it.name }
    }
}
