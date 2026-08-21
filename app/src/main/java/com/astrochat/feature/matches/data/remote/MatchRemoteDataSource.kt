package com.astrochat.feature.matches.data.remote

import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.core.common.toAppError
import com.astrochat.feature.matches.data.remote.dto.RandomUserResponse
import javax.inject.Inject

class MatchRemoteDataSource @Inject constructor(
    private val api: MatchApi
) {
    suspend fun getMatches(page: Int, pageSize: Int, seed: String? = null): DataResult<RandomUserResponse> {
        return try {
            val response = api.getMatches(results = pageSize, page = page, seed = seed)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(e.toAppError())
        }
    }
}
