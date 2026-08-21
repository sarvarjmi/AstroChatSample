package com.astrochat.feature.matches.data.repository

import androidx.paging.*
import com.astrochat.core.common.DataResult
import com.astrochat.core.common.di.IoDispatcher
import com.astrochat.core.common.toAppError
import com.astrochat.feature.matches.data.local.MatchLocalDataSource
import com.astrochat.feature.matches.data.mapper.toDomain
import com.astrochat.feature.matches.data.remote.MatchRemoteMediator
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import com.astrochat.feature.matches.domain.model.SyncStatus
import com.astrochat.feature.matches.domain.repository.MatchRepository
import com.astrochat.core.database.dao.MatchDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val matchDao: MatchDao,
    private val localDataSource: MatchLocalDataSource,
    private val remoteMediator: MatchRemoteMediator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MatchRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getMatches(): Flow<PagingData<MatchProfile>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { matchDao.getPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun updateMatchDecision(profileId: String, decision: MatchDecision): DataResult<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            localDataSource.updateMatchDecision(profileId, decision, SyncStatus.PENDING_SYNC)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e.toAppError())
        }
    }
}
