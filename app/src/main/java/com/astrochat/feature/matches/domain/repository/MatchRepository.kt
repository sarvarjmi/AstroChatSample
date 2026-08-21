package com.astrochat.feature.matches.domain.repository

import com.astrochat.core.common.DataResult
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getMatches(page: Int, pageSize: Int, seed: String? = null): Flow<DataResult<List<MatchProfile>>>
    suspend fun updateMatchDecision(profileId: String, decision: MatchDecision): DataResult<Unit>
}
