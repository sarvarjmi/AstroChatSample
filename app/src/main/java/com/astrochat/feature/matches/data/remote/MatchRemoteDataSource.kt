package com.astrochat.feature.matches.data.remote

import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.data.remote.dto.RandomUserResponse
import com.astrochat.feature.matches.data.remote.dto.UserDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MatchRemoteDataSource @Inject constructor(
    private val api: MatchApi
) {
    suspend fun getMatches(page: Int, pageSize: Int, seed: String? = null): DataResult<RandomUserResponse> {
        return try {
            val response = api.getMatches(results = pageSize, page = page, seed = seed)
            DataResult.Success(response)
        } catch (e: IOException) {
            DataResult.Error(AppError.Network.NoConnection)
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> DataResult.Error(AppError.NotFound)
                in 500..599 -> DataResult.Error(AppError.Network.Server)
                else -> DataResult.Error(AppError.Network.Unknown)
            }
        } catch (e: Exception) {
            DataResult.Error(AppError.Unknown)
        }
    }
}
