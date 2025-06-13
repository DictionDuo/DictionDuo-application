package com.example.androidlab2.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RecordHistoryEntity)

    @Update
    suspend fun update(record: RecordHistoryEntity)

    @Delete
    suspend fun delete(record: RecordHistoryEntity)

    @Query("SELECT * FROM record_history ORDER BY id DESC")
    fun getAllRecords(): Flow<List<RecordHistoryEntity>>

    @Query("DELETE FROM record_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM record_history WHERE title LIKE '%' || :title || '%'")
    suspend fun searchByTitle(title: String): List<RecordHistoryEntity>
}
