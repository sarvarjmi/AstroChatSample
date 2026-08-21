package com.astrochat.feature.matches.domain.repository

import androidx.paging.PagingData
import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getMatches(): Flow<PagingData<MatchProfile>>
    suspend fun updateMatchDecision(profileId: String, decision: MatchDecision): DataResult<Unit>
}
