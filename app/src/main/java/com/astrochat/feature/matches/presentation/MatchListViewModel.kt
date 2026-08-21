package com.astrochat.feature.matches.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.astrochat.core.common.onError
import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchListViewModel @Inject constructor(
    private val repository: MatchRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchListUiState())
    val uiState: StateFlow<MatchListUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MatchListUiEffect>()
    val uiEffect: SharedFlow<MatchListUiEffect> = _uiEffect.asSharedFlow()

    val matches = repository.getMatches()
        .cachedIn(viewModelScope)

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityObserver.observe()
            .onEach { status ->
                _uiState.update { it.copy(connectivityStatus = status) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: MatchListUiEvent) {
        when (event) {
            is MatchListUiEvent.Refresh -> {
                // Handled via matches.refresh() in UI
            }
            is MatchListUiEvent.Retry -> {
                // Handled via matches.retry() in UI
            }
            is MatchListUiEvent.AcceptMatch -> {
                updateDecision(event.profileId, MatchDecision.ACCEPTED)
            }
            is MatchListUiEvent.DeclineMatch -> {
                updateDecision(event.profileId, MatchDecision.DECLINED)
            }
            is MatchListUiEvent.DismissMessage -> {
                // Optional: handle message dismissal state if needed
            }
        }
    }

    private fun updateDecision(profileId: String, decision: MatchDecision) {
        viewModelScope.launch {
            repository.updateMatchDecision(profileId, decision)
                .onError {
                    _uiEffect.emit(MatchListUiEffect.ShowMessage("Failed to update decision locally"))
                }
        }
    }
}
