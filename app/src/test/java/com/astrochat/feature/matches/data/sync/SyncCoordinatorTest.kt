package com.astrochat.feature.matches.data.sync

import com.astrochat.core.database.dao.SyncOperationDao
import com.astrochat.core.database.entity.SyncOperationEntity
import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.data.local.MatchLocalDataSource
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncCoordinatorTest {

    private val syncOperationDao: SyncOperationDao = mockk()
    private val localDataSource: MatchLocalDataSource = mockk()
    private val connectivityObserver: ConnectivityObserver = mockk()
    private lateinit var syncCoordinator: SyncCoordinator

    @Before
    fun setup() {
        syncCoordinator = SyncCoordinator(
            syncOperationDao,
            localDataSource,
            connectivityObserver,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun `syncPendingOperations updates local data when online`() = runTest {
        val pendingOperation = SyncOperationEntity(
            operationId = 1,
            profileId = "id-1",
            decision = MatchDecision.ACCEPTED,
            attemptCount = 0
        )

        coEvery { syncOperationDao.getPendingOperations() } returns flowOf(listOf(pendingOperation))
        coEvery { syncOperationDao.incrementAttemptCount(any(), any()) } returns Unit
        coEvery { localDataSource.updateMatchDecision(any(), any(), any()) } returns Unit

        syncCoordinator.syncPendingOperations()

        coVerify {
            localDataSource.updateMatchDecision(
                profileId = "id-1",
                decision = MatchDecision.ACCEPTED,
                syncStatus = SyncStatus.SYNCED
            )
        }
    }
}
