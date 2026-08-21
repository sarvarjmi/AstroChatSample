package com.astrochat.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.astrochat.core.database.dao.MatchDao
import com.astrochat.core.database.dao.SyncOperationDao
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.core.database.entity.SyncOperationEntity
import com.astrochat.core.database.util.Converters

@Database(
    entities = [MatchEntity::class, SyncOperationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MatchMateDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun syncOperationDao(): SyncOperationDao
}
