package com.astrochat.feature.matches.data.local

import androidx.room.Transaction
import com.astrochat.core.database.dao.MatchDao
import com.astrochat.core.database.dao.SyncOperationDao
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.core.database.entity.SyncOperationEntity
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchLocalDataSource @Inject constructor(
    private val matchDao: MatchDao,
    private val syncOperationDao: SyncOperationDao
) {
    fun getMatches(): Flow<List<MatchEntity>> = matchDao.getMatches()

    suspend fun insertMatches(matches: List<MatchEntity>) =
        matchDao.upsertMatchesPreservingDecisions(matches)

    @Transaction
    suspend fun updateMatchDecision(profileId: String, decision: MatchDecision, syncStatus: SyncStatus) {
        matchDao.updateDecision(profileId, decision, syncStatus)
        if (syncStatus == SyncStatus.PENDING_SYNC) {
            syncOperationDao.insertOperation(
                SyncOperationEntity(profileId = profileId, decision = decision)
            )
        } else if (syncStatus == SyncStatus.SYNCED) {
            syncOperationDao.deleteOperationsForProfile(profileId)
        }
    }

    suspend fun getMaxPageIndex(): Int = matchDao.getMaxPageIndex() ?: 0

    suspend fun clearAll() = matchDao.clearAll()
}
