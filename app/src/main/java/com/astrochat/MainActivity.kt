package com.astrochat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.astrochat.feature.matches.presentation.MatchListViewModel
import com.astrochat.feature.matches.presentation.MatchesRoute
import com.astrochat.ui.theme.MatchMateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MatchListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MatchMateTheme(darkTheme = uiState.isDarkMode) {
                MatchesRoute(viewModel = viewModel)
            }
        }
    }
}
