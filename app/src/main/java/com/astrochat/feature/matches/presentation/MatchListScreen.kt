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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.astrochat.core.common.AppError
import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.presentation.components.MatchCard

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

    LaunchedEffect(matches.loadState.refresh) {
        val loadState = matches.loadState.refresh
        if (loadState is LoadState.Error && matches.itemCount > 0) {
            val error = loadState.error as? AppError
            snackbarHostState.showSnackbar(
                message = error?.getUserFriendlyMessage() ?: "Failed to refresh matches."
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MatchListUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MatchMate") },
                actions = {
                    IconButton(onClick = { matches.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AnimatedVisibility(
                visible = uiState.connectivityStatus != ConnectivityObserver.Status.Available,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                ConnectivityBanner(status = uiState.connectivityStatus)
            }

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
                                onAccept = { viewModel.onEvent(MatchListUiEvent.AcceptMatch(profile.id)) },
                                onDecline = { viewModel.onEvent(MatchListUiEvent.DeclineMatch(profile.id)) }
                            )
                        }
                    }

                    when (val loadState = matches.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is LoadState.Error -> {
                            val error = loadState.error as? AppError
                            item {
                                ErrorItem(
                                    message = error?.getUserFriendlyMessage() ?: "Failed to load more profiles",
                                    onRetry = { matches.retry() }
                                )
                            }
                        }
                        else -> {}
                    }
                }

                if (matches.loadState.refresh is LoadState.Loading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                if (matches.loadState.refresh is LoadState.Error && matches.itemCount == 0) {
                    val error = (matches.loadState.refresh as LoadState.Error).error as? AppError
                    ErrorView(
                        message = error?.getUserFriendlyMessage() ?: "Could not fetch matches.",
                        onRetry = { matches.retry() }
                    )
                }

                if (matches.loadState.refresh is LoadState.NotLoading && matches.itemCount == 0) {
                    EmptyView()
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
            text = "Offline Mode - $status",
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
            Text("Retry")
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No profiles found.")
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectivityBannerPreview() {
    ConnectivityBanner(status = ConnectivityObserver.Status.Unavailable)
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    ErrorView(message = "Sample error message", onRetry = {})
}

@Preview(showBackground = true)
@Composable
fun EmptyViewPreview() {
    EmptyView()
}
