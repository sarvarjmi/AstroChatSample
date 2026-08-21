package com.astrochat.feature.matches.data.remote

import com.astrochat.feature.matches.data.remote.dto.RandomUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchApi {
    @GET("api/")
    suspend fun getMatches(
        @Query("results") results: Int,
        @Query("page") page: Int,
        @Query("seed") seed: String? = null
    ): RandomUserResponse

    companion object {
        const val BASE_URL = "https://randomuser.me/"
    }
}
