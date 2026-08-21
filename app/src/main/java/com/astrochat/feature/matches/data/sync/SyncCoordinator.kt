package com.astrochat.feature.matches.data.sync

import com.astrochat.core.common.di.IoDispatcher
import com.astrochat.core.database.dao.SyncOperationDao
import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.data.local.MatchLocalDataSource
import com.astrochat.feature.matches.domain.model.SyncStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class SyncCoordinator @Inject constructor(
    private val syncOperationDao: SyncOperationDao,
    private val localDataSource: MatchLocalDataSource,
    private val connectivityObserver: ConnectivityObserver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    fun initialize() {
        scope.launch {
            combine(
                connectivityObserver.observe(),
                syncOperationDao.getPendingOperations()
            ) { status, operations ->
                status == ConnectivityObserver.Status.Available && operations.isNotEmpty()
            }.collectLatest { shouldSync ->
                if (shouldSync) {
                     syncPendingOperations()
                }
            }
        }
    }

    suspend fun syncPendingOperations() {
        val pending = syncOperationDao.getPendingOperations().first()
        pending.forEach { operation ->
            // Retry policy implementation:
            // If it failed too many times, we mark it as SYNC_FAILED.
            if (operation.attemptCount > 3) {
                localDataSource.updateMatchDecision(
                    profileId = operation.profileId,
                    decision = operation.decision,
                    syncStatus = SyncStatus.SYNC_FAILED
                )
                return@forEach
            }

            try {
                // Simulate exponential backoff
                if (operation.attemptCount > 0) {
                    delay((1 shl (operation.attemptCount - 1)).seconds)
                }

                syncOperationDao.incrementAttemptCount(operation.operationId)

                // IMPORTANT: The supplied Random User API is read-only and does not
                // provide an endpoint for Accept/Decline mutations.
                // In a real application, the network call to the backend would happen here:
                // val result = api.updateMatchDecision(operation.profileId, operation.decision)

                // For this assignment, we simulate a successful server-side synchronization:
                localDataSource.updateMatchDecision(
                    profileId = operation.profileId,
                    decision = operation.decision,
                    syncStatus = SyncStatus.SYNCED
                )
            } catch (e: Exception) {
                // Keep as PENDING_SYNC for the next trigger
            }
        }
    }
}
