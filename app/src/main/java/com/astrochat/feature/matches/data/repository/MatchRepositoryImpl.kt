package com.astrochat.feature.matches.data.repository

import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.data.mapper.toDomain
import com.astrochat.feature.matches.data.remote.MatchRemoteDataSource
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import com.astrochat.feature.matches.domain.repository.MatchRepository
import com.astrochat.core.common.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val remoteDataSource: MatchRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MatchRepository {

    override fun getMatches(page: Int, pageSize: Int, seed: String?): Flow<DataResult<List<MatchProfile>>> = flow {
        emit(DataResult.Loading)
        val result = remoteDataSource.getMatches(page, pageSize, seed)
        when (result) {
            is DataResult.Success -> {
                val domainList = result.data.results.map { it.toDomain() }
                emit(DataResult.Success(domainList))
            }
            is DataResult.Error -> {
                emit(DataResult.Error(result.error))
            }
            else -> {}
        }
    }.flowOn(ioDispatcher)

    override suspend fun updateMatchDecision(profileId: String, decision: MatchDecision): DataResult<Unit> {
        // Local persistence and sync logic will be added in Phase 3 & 4
        return DataResult.Success(Unit)
    }
}
