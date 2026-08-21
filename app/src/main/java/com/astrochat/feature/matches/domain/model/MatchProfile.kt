package com.astrochat.feature.matches.domain.model

data class MatchProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageUrl: String,
    val decision: MatchDecision = MatchDecision.PENDING,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

enum class MatchDecision {
    PENDING,
    ACCEPTED,
    DECLINED
}

enum class SyncStatus {
    SYNCED,
    PENDING_SYNC,
    SYNC_FAILED
}
