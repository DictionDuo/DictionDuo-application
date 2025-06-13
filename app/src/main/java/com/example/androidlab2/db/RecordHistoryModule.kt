package com.example.androidlab2.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecordHistoryModule {

    @Provides
    @Singleton
    fun provideRecordHistoryDatabase(
        @ApplicationContext context: Context
    ): RecordHistoryDatabase {
        return Room.databaseBuilder(
            context,
            RecordHistoryDatabase::class.java,
            "record_history.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecordHistoryDao(database: RecordHistoryDatabase): RecordHistoryDao {
        return database.recordHistoryDao()
    }

    @Provides
    @Singleton
    fun provideRecordRepository(dao: RecordHistoryDao): RecordRepository {
        return RecordRepository(dao)
    }
}
