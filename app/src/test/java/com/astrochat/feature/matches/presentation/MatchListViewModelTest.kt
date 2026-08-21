package com.astrochat.feature.matches.presentation

import app.cash.turbine.test
import androidx.paging.PagingData
import com.astrochat.core.common.AppError
import com.astrochat.core.common.DataResult
import com.astrochat.core.network.ConnectivityObserver
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.repository.MatchRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MatchListViewModelTest {

    private val repository: MatchRepository = mockk()
    private val connectivityObserver: ConnectivityObserver = mockk()
    private lateinit var viewModel: MatchListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val connectivityFlow = MutableSharedFlow<ConnectivityObserver.Status>(replay = 1)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getMatches() } returns flowOf(PagingData.empty())
        every { connectivityObserver.observe() } returns connectivityFlow
        connectivityFlow.tryEmit(ConnectivityObserver.Status.Available)

        viewModel = MatchListViewModel(repository, connectivityObserver)
    }

    @Test
    fun `initial state has correct connectivity status`() = runTest {
        assertEquals(ConnectivityObserver.Status.Available, viewModel.uiState.value.connectivityStatus)
    }

    @Test
    fun `AcceptMatch event triggers repository update`() = runTest {
        coEvery { repository.updateMatchDecision(any(), MatchDecision.ACCEPTED) } returns DataResult.Success(Unit)

        viewModel.onEvent(MatchListUiEvent.AcceptMatch("1"))

        io.mockk.coVerify { repository.updateMatchDecision("1", MatchDecision.ACCEPTED) }
    }

    @Test
    fun `DeclineMatch event triggers repository update`() = runTest {
        coEvery { repository.updateMatchDecision(any(), MatchDecision.DECLINED) } returns DataResult.Success(Unit)

        viewModel.onEvent(MatchListUiEvent.DeclineMatch("1"))

        io.mockk.coVerify { repository.updateMatchDecision("1", MatchDecision.DECLINED) }
    }

    @Test
    fun `connectivity changes update uiState`() = runTest {
        connectivityFlow.emit(ConnectivityObserver.Status.Lost)
        assertEquals(ConnectivityObserver.Status.Lost, viewModel.uiState.value.connectivityStatus)
    }

    @Test
    fun `updateMatchDecision error triggers ShowMessage effect`() = runTest {
        coEvery { repository.updateMatchDecision(any(), any()) } returns DataResult.Error(AppError.Unknown)

        viewModel.uiEffect.test {
            viewModel.onEvent(MatchListUiEvent.AcceptMatch("1"))
            val effect = awaitItem()
            assertTrue(effect is MatchListUiEffect.ShowMessage)
            assertEquals(AppError.Unknown.getUserFriendlyMessage(), (effect as MatchListUiEffect.ShowMessage).message)
        }
    }
}
