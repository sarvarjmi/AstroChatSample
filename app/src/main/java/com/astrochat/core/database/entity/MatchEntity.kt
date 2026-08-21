package com.astrochat.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageUrl: String,
    val decision: MatchDecision,
    val syncStatus: SyncStatus,
    val pageIndex: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
