package com.astrochat.feature.matches.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import com.astrochat.feature.matches.domain.model.SyncStatus
import org.junit.Rule
import org.junit.Test

class MatchCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun matchCard_displaysProfileInfo() {
        val profile = MatchProfile(
            id = "1",
            firstName = "John",
            lastName = "Doe",
            age = 30,
            city = "NY",
            state = "NY",
            country = "USA",
            imageUrl = "url",
            decision = MatchDecision.PENDING,
            syncStatus = SyncStatus.SYNCED
        )

        composeTestRule.setContent {
            MatchCard(
                profile = profile,
                onAccept = {},
                onDecline = {}
            )
        }

        composeTestRule.onNodeWithText("John Doe, 30").assertIsDisplayed()
        composeTestRule.onNodeWithText("NY, NY").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accept").assertIsDisplayed()
        composeTestRule.onNodeWithText("Decline").assertIsDisplayed()
    }
}
