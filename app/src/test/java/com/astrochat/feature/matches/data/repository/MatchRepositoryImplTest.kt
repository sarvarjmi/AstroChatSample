package com.astrochat.feature.matches.data.repository

import app.cash.turbine.test
import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.data.local.MatchLocalDataSource
import com.astrochat.feature.matches.data.remote.MatchRemoteDataSource
import com.astrochat.feature.matches.data.remote.dto.RandomUserResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatchRepositoryImplTest {

    private val remoteDataSource: MatchRemoteDataSource = mockk()
    private val localDataSource: MatchLocalDataSource = mockk()
    private lateinit var repository: MatchRepositoryImpl

    @Before
    fun setup() {
        repository = MatchRepositoryImpl(
            remoteDataSource,
            localDataSource,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun `getMatches emits loading then success from local data`() = runTest {
        val remoteResponse = mockk<RandomUserResponse>()
        coEvery { remoteResponse.results } returns emptyList()
        coEvery { remoteDataSource.getMatches(any(), any(), any()) } returns DataResult.Success(remoteResponse)
        coEvery { localDataSource.insertMatches(any()) } returns Unit
        coEvery { localDataSource.getMatches() } returns flowOf(emptyList())

        repository.getMatches(1, 10).test {
            assertTrue(awaitItem() is DataResult.Loading)
            assertTrue(awaitItem() is DataResult.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMatches emits error when local is empty and remote fails`() = runTest {
        coEvery { remoteDataSource.getMatches(any(), any(), any()) } returns DataResult.Error(AppError.Network.NoConnection)
        coEvery { localDataSource.getMatches() } returns flowOf(emptyList())

        repository.getMatches(1, 10).test {
            assertTrue(awaitItem() is DataResult.Loading)
            // Depending on execution order, the empty Success from DB might or might not come before Error
            // In MatchRepositoryImpl, we launch dbJob, then do network fetch.

            val item2 = awaitItem()
            if (item2 is DataResult.Error) {
                assertEquals(AppError.Network.NoConnection, item2.error)
            } else {
                assertTrue(item2 is DataResult.Success)
                val item3 = awaitItem()
                assertTrue(item3 is DataResult.Error)
                assertEquals(AppError.Network.NoConnection, (item3 as DataResult.Error).error)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
