package com.astrochat.core.database.di

import android.content.Context
import androidx.room.Room
import com.astrochat.core.database.MatchMateDatabase
import com.astrochat.core.database.dao.MatchDao
import com.astrochat.core.database.dao.SyncOperationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MatchMateDatabase {
        return Room.databaseBuilder(
            context,
            MatchMateDatabase::class.java,
            "matchmate_db"
        ).build()
    }

    @Provides
    fun provideMatchDao(database: MatchMateDatabase): MatchDao {
        return database.matchDao()
    }

    @Provides
    fun provideSyncOperationDao(database: MatchMateDatabase): SyncOperationDao {
        return database.syncOperationDao()
    }
}
