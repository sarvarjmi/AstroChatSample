package com.astrochat.feature.matches.data.remote

import androidx.paging.*
import com.astrochat.core.common.DataResult
import com.astrochat.core.database.dao.MatchDao
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.feature.matches.data.remote.dto.InfoDto
import com.astrochat.feature.matches.data.remote.dto.RandomUserResponse
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
class MatchRemoteMediatorTest {

    private val remoteDataSource: MatchRemoteDataSource = mockk()
    private val matchDao: MatchDao = mockk()
    private lateinit var mediator: MatchRemoteMediator

    @Before
    fun setup() {
        mediator = MatchRemoteMediator(remoteDataSource, matchDao)
    }

    @Test
    fun `refresh load returns Success when remote data is fetched`() = runTest {
        val remoteResponse = RandomUserResponse(
            results = emptyList(),
            info = InfoDto("seed", 1)
        )
        coEvery { remoteDataSource.getMatches(any(), any(), any()) } returns DataResult.Success(remoteResponse)
        coEvery { matchDao.upsertMatchesPreservingDecisions(any()) } returns Unit

        val pagingState = PagingState<Int, MatchEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load returns Error when remote fetch fails`() = runTest {
        coEvery { remoteDataSource.getMatches(any(), any(), any()) } returns DataResult.Error(com.astrochat.core.common.AppError.Network.Server)

        val pagingState = PagingState<Int, MatchEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }
}
