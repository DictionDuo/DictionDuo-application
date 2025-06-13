package com.example.androidlab2.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val repository: RecordRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<RecordHistoryEntity>>(emptyList())
    val records: StateFlow<List<RecordHistoryEntity>> = _records.asStateFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            repository.getAllRecords().collect { list ->
                _records.value = list
            }
        }
    }

    fun insertRecord(record: RecordHistoryEntity) {
        viewModelScope.launch {
            repository.insert(record)
        }
    }

    fun deleteRecord(record: RecordHistoryEntity) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    fun updateRecord(record: RecordHistoryEntity) {
        viewModelScope.launch {
            repository.update(record)
        }
    }
}

