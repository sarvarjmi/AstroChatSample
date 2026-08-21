package com.astrochat.feature.matches.data.repository

import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.core.common.di.IoDispatcher
import com.astrochat.feature.matches.data.local.MatchLocalDataSource
import com.astrochat.feature.matches.data.mapper.toDomain
import com.astrochat.feature.matches.data.mapper.toEntity
import com.astrochat.feature.matches.data.remote.MatchRemoteDataSource
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import com.astrochat.feature.matches.domain.model.SyncStatus
import com.astrochat.feature.matches.domain.repository.MatchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val remoteDataSource: MatchRemoteDataSource,
    private val localDataSource: MatchLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MatchRepository {

    override fun getMatches(page: Int, pageSize: Int, seed: String?): Flow<DataResult<List<MatchProfile>>> = channelFlow {
        send(DataResult.Loading)

        // Observe the database and send updates to the channel immediately
        val dbJob = launch {
            localDataSource.getMatches().collect { entities ->
                send(DataResult.Success(entities.map { it.toDomain() }))
            }
        }

        // Trigger remote fetch and update DB in the background
        val remoteResult = remoteDataSource.getMatches(page, pageSize, seed)
        if (remoteResult is DataResult.Success) {
            val entities = remoteResult.data.results.map {
                it.toDomain().toEntity(pageIndex = page)
            }
            localDataSource.insertMatches(entities)
        } else if (remoteResult is DataResult.Error) {
            // If the database is empty, propagate the network error
            val currentMatches = localDataSource.getMatches().first()
            if (currentMatches.isEmpty()) {
                send(DataResult.Error(remoteResult.error))
            }
        }

        // Wait for the dbJob to complete (it won't unless the flow is cancelled)
        dbJob.join()
    }.flowOn(ioDispatcher)

    override suspend fun updateMatchDecision(profileId: String, decision: MatchDecision): DataResult<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            localDataSource.updateMatchDecision(profileId, decision, SyncStatus.PENDING_SYNC)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(AppError.Unknown)
        }
    }
}
