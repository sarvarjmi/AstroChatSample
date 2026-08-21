package com.astrochat.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY pageIndex ASC, createdAt ASC")
    fun getMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches ORDER BY pageIndex ASC, createdAt ASC")
    fun getPagingSource(): PagingSource<Int, MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(matches: List<MatchEntity>): List<Long>

    @Update
    suspend fun updateMatches(matches: List<MatchEntity>)

    @Transaction
    suspend fun upsertMatchesPreservingDecisions(newMatches: List<MatchEntity>) {
        val insertResults = insertIgnore(newMatches)

        val toUpdate = mutableListOf<MatchEntity>()
        insertResults.forEachIndexed { index, result ->
            if (result == -1L) { // Already exists
                val newMatch = newMatches[index]
                val existingMatch = getMatchById(newMatch.id)
                if (existingMatch != null) {
                    // Preserve existing decision and sync status
                    toUpdate.add(newMatch.copy(
                        decision = existingMatch.decision,
                        syncStatus = existingMatch.syncStatus,
                        createdAt = existingMatch.createdAt
                    ))
                }
            }
        }
        if (toUpdate.isNotEmpty()) {
            updateMatches(toUpdate)
        }
    }

    @Query("UPDATE matches SET decision = :decision, syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDecision(id: String, decision: MatchDecision, syncStatus: SyncStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM matches")
    suspend fun clearAll()

    @Query("SELECT MAX(pageIndex) FROM matches")
    suspend fun getMaxPageIndex(): Int?
}
