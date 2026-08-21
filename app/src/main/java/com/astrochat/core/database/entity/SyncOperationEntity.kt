package com.astrochat.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.astrochat.feature.matches.domain.model.MatchDecision

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey(autoGenerate = true) val operationId: Long = 0,
    val profileId: String,
    val decision: MatchDecision,
    val createdAt: Long = System.currentTimeMillis(),
    val attemptCount: Int = 0,
    val lastAttemptAt: Long = 0
)
