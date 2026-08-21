package com.astrochat.feature.matches.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.astrochat.R
import com.astrochat.core.common.AppError
import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.domain.model.MatchProfile
import com.astrochat.feature.matches.presentation.components.MatchCard
import com.astrochat.ui.theme.MatchMateTheme

@Composable
fun MatchesRoute(
    viewModel: MatchListViewModel = hiltViewModel()
) {
    MatchListScreen(viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchListScreen(
    viewModel: MatchListViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val matches = viewModel.matches.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleRefreshError(matches, snackbarHostState)
    HandleUiEffects(viewModel.uiEffect, snackbarHostState)

    Scaffold(
        topBar = {
            MatchListTopBar(onRefresh = { matches.refresh() })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ConnectivityBannerVisibility(uiState.connectivityStatus)

            MatchListContent(
                matches = matches,
                onAccept = { id -> viewModel.onEvent(MatchListUiEvent.AcceptMatch(id)) },
                onDecline = { id -> viewModel.onEvent(MatchListUiEvent.DeclineMatch(id)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchListTopBar(onRefresh: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_matches)) },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.action_refresh))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun ConnectivityBannerVisibility(status: ConnectivityObserver.Status) {
    AnimatedVisibility(
        visible = status != ConnectivityObserver.Status.Available,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        ConnectivityBanner(status = status)
    }
}

@Composable
private fun MatchListContent(
    matches: LazyPagingItems<MatchProfile>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(
                count = matches.itemCount,
                key = matches.itemKey { it.id }
            ) { index ->
                val profile = matches[index]
                if (profile != null) {
                    MatchCard(
                        profile = profile,
                        onAccept = { onAccept(profile.id) },
                        onDecline = { onDecline(profile.id) }
                    )
                }
            }

            item {
                AppendLoadStateItem(matches.loadState.append) { matches.retry() }
            }
        }

        RefreshLoadStateOverlay(matches)
    }
}

@Composable
private fun AppendLoadStateItem(
    loadState: LoadState,
    onRetry: () -> Unit
) {
    when (loadState) {
        is LoadState.Loading -> {
            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            val error = loadState.error as? AppError
            ErrorItem(
                message = error?.getUserFriendlyMessage() ?: stringResource(R.string.error_load_more),
                onRetry = onRetry
            )
        }
        else -> {}
    }
}

@Composable
private fun RefreshLoadStateOverlay(matches: LazyPagingItems<MatchProfile>) {
    val refreshState = matches.loadState.refresh

    if (refreshState is LoadState.Loading) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    if (refreshState is LoadState.Error && matches.itemCount == 0) {
        val error = refreshState.error as? AppError
        ErrorView(
            message = error?.getUserFriendlyMessage() ?: stringResource(R.string.error_fetch_matches),
            onRetry = { matches.retry() }
        )
    }

    if (refreshState is LoadState.NotLoading && matches.itemCount == 0) {
        EmptyView()
    }
}

@Composable
private fun HandleRefreshError(
    matches: LazyPagingItems<MatchProfile>,
    snackbarHostState: SnackbarHostState
) {
    val errorMessage = stringResource(R.string.error_refresh_matches)
    LaunchedEffect(matches.loadState.refresh) {
        val loadState = matches.loadState.refresh
        if (loadState is LoadState.Error && matches.itemCount > 0) {
            val error = loadState.error as? AppError
            snackbarHostState.showSnackbar(
                message = error?.getUserFriendlyMessage() ?: errorMessage
            )
        }
    }
}

@Composable
private fun HandleUiEffects(
    uiEffect: kotlinx.coroutines.flow.SharedFlow<MatchListUiEffect>,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is MatchListUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }
}

@Composable
fun ConnectivityBanner(status: ConnectivityObserver.Status) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (status == ConnectivityObserver.Status.Unavailable || status == ConnectivityObserver.Status.Lost) Color.Red else Color.Gray)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.status_offline, status.name),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ErrorItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.empty_profiles))
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectivityBannerPreview() {
    MatchMateTheme {
        ConnectivityBanner(status = ConnectivityObserver.Status.Unavailable)
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    MatchMateTheme {
        ErrorView(message = "Sample error message", onRetry = {})
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyViewPreview() {
    MatchMateTheme {
        EmptyView()
    }
}
