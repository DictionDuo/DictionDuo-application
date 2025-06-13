package com.example.androidlab2.db

import kotlinx.coroutines.flow.Flow

class RecordRepository(private val dao: RecordHistoryDao) {

    fun getAllRecords(): Flow<List<RecordHistoryEntity>> = dao.getAllRecords()

    suspend fun insert(record: RecordHistoryEntity) {
        dao.insert(record)
    }

    suspend fun update(record: RecordHistoryEntity) {
        dao.update(record)
    }

    suspend fun delete(record: RecordHistoryEntity) {
        dao.delete(record)
    }
}