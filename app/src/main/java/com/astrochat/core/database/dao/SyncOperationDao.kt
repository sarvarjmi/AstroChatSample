package com.astrochat.core.database.dao

import androidx.room.*
import com.astrochat.core.database.entity.SyncOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOperationDao {
    @Query("SELECT * FROM sync_operations ORDER BY createdAt ASC")
    fun getPendingOperations(): Flow<List<SyncOperationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: SyncOperationEntity)

    @Delete
    suspend fun deleteOperation(operation: SyncOperationEntity)

    @Query("UPDATE sync_operations SET attemptCount = attemptCount + 1, lastAttemptAt = :timestamp WHERE operationId = :id")
    suspend fun incrementAttemptCount(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM sync_operations WHERE profileId = :profileId")
    suspend fun deleteOperationsForProfile(profileId: String)
}
