package com.astrochat.feature.matches.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.astrochat.R
import com.astrochat.feature.matches.domain.model.MatchDecision
import com.astrochat.feature.matches.domain.model.MatchProfile
import com.astrochat.feature.matches.domain.model.SyncStatus
import com.astrochat.ui.theme.MatchMateTheme

@Composable
fun MatchCard(
    profile: MatchProfile,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardContentDesc = stringResource(
        R.string.desc_match_card,
        profile.firstName,
        profile.lastName,
        profile.age,
        profile.city
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .semantics { contentDescription = cardContentDesc },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()) {
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = stringResource(R.string.desc_profile_image, profile.firstName),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )

                if (profile.decision != MatchDecision.PENDING) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                        color = if (profile.decision == MatchDecision.ACCEPTED) Color.Green.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = profile.decision.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${profile.firstName} ${profile.lastName}, ${profile.age}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${profile.city}, ${profile.state}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        enabled = profile.decision == MatchDecision.PENDING
                    ) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_decline))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.action_decline))
                    }

                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.White),
                        enabled = profile.decision == MatchDecision.PENDING
                    ) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.action_accept))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.action_accept))
                    }
                }

                if (profile.syncStatus == SyncStatus.PENDING_SYNC) {
                    Text(
                        text = stringResource(R.string.status_syncing),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchCardPendingPreview() {
    MatchMateTheme {
        MatchCard(
            profile = MatchProfile(
                id = "1",
                firstName = "John",
                lastName = "Doe",
                age = 30,
                city = "New York",
                state = "NY",
                country = "USA",
                imageUrl = "",
                decision = MatchDecision.PENDING,
                syncStatus = SyncStatus.SYNCED
            ),
            onAccept = {},
            onDecline = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchCardAcceptedPreview() {
    MatchMateTheme {
        MatchCard(
            profile = MatchProfile(
                id = "2",
                firstName = "Jane",
                lastName = "Smith",
                age = 28,
                city = "Los Angeles",
                state = "CA",
                country = "USA",
                imageUrl = "",
                decision = MatchDecision.ACCEPTED,
                syncStatus = SyncStatus.SYNCED
            ),
            onAccept = {},
            onDecline = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchCardSyncingPreview() {
    MatchMateTheme {
        MatchCard(
            profile = MatchProfile(
                id = "3",
                firstName = "Alex",
                lastName = "Brown",
                age = 32,
                city = "Chicago",
                state = "IL",
                country = "USA",
                imageUrl = "",
                decision = MatchDecision.DECLINED,
                syncStatus = SyncStatus.PENDING_SYNC
            ),
            onAccept = {},
            onDecline = {}
        )
    }
}
