package com.astrochat.feature.matches.data.remote

import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.data.remote.dto.UserDto
import javax.inject.Inject

class MatchRemoteDataSource @Inject constructor(
    private val api: MatchApi
) {
    suspend fun getMatches(page: Int, pageSize: Int): DataResult<List<UserDto>> {
        return try {
            val response = api.getMatches(results = pageSize, page = page)
            DataResult.Success(response.results)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
