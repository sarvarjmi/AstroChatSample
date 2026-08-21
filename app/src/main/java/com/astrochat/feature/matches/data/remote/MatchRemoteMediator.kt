package com.astrochat.feature.matches.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.astrochat.core.common.DataResult
import com.astrochat.core.database.dao.MatchDao
import com.astrochat.core.database.entity.MatchEntity
import com.astrochat.feature.matches.data.mapper.toDomain
import com.astrochat.feature.matches.data.mapper.toEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MatchRemoteMediator @Inject constructor(
    private val remoteDataSource: MatchRemoteDataSource,
    private val matchDao: MatchDao
) : RemoteMediator<Int, MatchEntity>() {

    // Stable seed for the session to ensure deterministic pagination
    private val sessionSeed = "matchmate_session_${System.currentTimeMillis() / 3600000}"

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MatchEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        lastItem.pageIndex + 1
                    }
                }
            }

            val result = remoteDataSource.getMatches(
                page = page,
                pageSize = state.config.pageSize,
                seed = sessionSeed
            )

            when (result) {
                is DataResult.Success -> {
                    val entities = result.data.results.map {
                        it.toDomain().toEntity(pageIndex = page)
                    }

                    matchDao.upsertMatchesPreservingDecisions(entities)

                    MediatorResult.Success(
                        endOfPaginationReached = result.data.results.isEmpty()
                    )
                }
                is DataResult.Error -> {
                    MediatorResult.Error(result.error)
                }
                else -> MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
