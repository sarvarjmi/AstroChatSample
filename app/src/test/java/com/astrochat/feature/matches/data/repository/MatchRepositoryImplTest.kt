package com.astrochat.feature.matches.data.repository

import androidx.paging.PagingSource
import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.core.database.dao.MatchDao
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.feature.matches.data.local.MatchLocalDataSource
import com.astrochat.feature.matches.data.remote.MatchRemoteMediator
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.SyncStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatchRepositoryImplTest {

    private val matchDao: MatchDao = mockk()
    private val localDataSource: MatchLocalDataSource = mockk()
    private val remoteMediator: MatchRemoteMediator = mockk()
    private lateinit var repository: MatchRepositoryImpl

    @Before
    fun setup() {
        repository = MatchRepositoryImpl(
            matchDao,
            localDataSource,
            remoteMediator,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun `getMatches returns flow of paging data`() = runTest {
        val pagingSource = mockk<PagingSource<Int, MatchEntity>>()
        coEvery { matchDao.getPagingSource() } returns pagingSource

        val result = repository.getMatches()

        // Verifying flow is returned (Smoke test for Paging 3 integration)
        assertTrue(result != null)
    }

    @Test
    fun `updateMatchDecision returns success when local update succeeds`() = runTest {
        coEvery { localDataSource.updateMatchDecision(any(), any(), any()) } returns Unit

        val result = repository.updateMatchDecision("1", MatchDecision.ACCEPTED)

        assertTrue(result is DataResult.Success)
    }

    @Test
    fun `updateMatchDecision returns error when local update fails`() = runTest {
        coEvery { localDataSource.updateMatchDecision(any(), any(), any()) } throws Exception("DB error")

        val result = repository.updateMatchDecision("1", MatchDecision.ACCEPTED)

        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).error is AppError.Unknown)
    }
}
