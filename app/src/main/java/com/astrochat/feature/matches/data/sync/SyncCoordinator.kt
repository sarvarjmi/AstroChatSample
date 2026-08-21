package com.astrochat.feature.matches.data.sync

import android.util.Log
import com.astrochat.core.common.AppError
import com.astrochat.core.common.di.IoDispatcher
import com.astrochat.core.common.toAppError
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
            Log.d("SyncCoordinator", "Syncing profile: ${operation.profileId}, attempt: ${operation.attemptCount}")

            if (operation.attemptCount > 3) {
                Log.e("SyncCoordinator", "Max retries reached for profile: ${operation.profileId}")
                localDataSource.updateMatchDecision(
                    profileId = operation.profileId,
                    decision = operation.decision,
                    syncStatus = SyncStatus.SYNC_FAILED
                )
                return@forEach
            }

            try {
                if (operation.attemptCount > 0) {
                    delay((1 shl (operation.attemptCount - 1)).seconds)
                }

                syncOperationDao.incrementAttemptCount(operation.operationId)

                // IMPORTANT: The supplied Random User API is read-only.
                // We simulate server synchronization here.

                localDataSource.updateMatchDecision(
                    profileId = operation.profileId,
                    decision = operation.decision,
                    syncStatus = SyncStatus.SYNCED
                )
                Log.i("SyncCoordinator", "Successfully synced profile: ${operation.profileId}")
            } catch (e: Exception) {
                val appError = e.toAppError()
                Log.w("SyncCoordinator", "Sync failed for profile: ${operation.profileId}. Error: ${appError.getUserFriendlyMessage()}")
                if (appError is AppError.Network.Server || appError is AppError.Unknown) {
                    // Retry on next connectivity change
                } else {
                    localDataSource.updateMatchDecision(
                        profileId = operation.profileId,
                        decision = operation.decision,
                        syncStatus = SyncStatus.SYNC_FAILED
                    )
                }
            }
        }
    }
}
