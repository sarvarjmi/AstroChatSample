package com.astrochat.feature.matches.presentation

import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.domain.model.MatchDecision

data class MatchListUiState(
    val connectivityStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.Unavailable
)

sealed class MatchListUiEvent {
    object Refresh : MatchListUiEvent()
    object Retry : MatchListUiEvent()
    data class AcceptMatch(val profileId: String) : MatchListUiEvent()
    data class DeclineMatch(val profileId: String) : MatchListUiEvent()
    object DismissMessage : MatchListUiEvent()
}

sealed class MatchListUiEffect {
    data class ShowMessage(val message: String) : MatchListUiEffect()
}
