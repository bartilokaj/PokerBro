package pl.blokaj.pokerbro.ui.screens.contents


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.items.contents.ProfilePicture
import pl.blokaj.pokerbro.ui.items.contents.TextInputField
import pl.blokaj.pokerbro.ui.screens.components.LobbySetupComponent

@Composable
fun HostingScreen(
    lobbySetupComponent: LobbySetupComponent
) {
    val playerName by lobbySetupComponent.playerName.subscribeAsState()
    val lobbyName by lobbySetupComponent.lobbyName.subscribeAsState()
    val startingFunds by lobbySetupComponent.startingFunds.subscribeAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Hosting Screen", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))
        ProfilePicture(lobbySetupComponent.profilePictureComponent)

        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PLAYER NAME",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                )
                TextInputField(
                    value = playerName,
                    onValueChange = { lobbySetupComponent.playerName.value = it },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOBBY NAME",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                )
                TextInputField(
                    value = lobbyName,
                    onValueChange = { lobbySetupComponent.lobbyName.value = it },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STARTING FUNDS",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                )
                TextInputField(
                    value = startingFunds,
                    onValueChange = { lobbySetupComponent.startingFunds.value = it },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                var valuesCheck = true
                if (lobbySetupComponent.playerName.value.isEmpty()) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Player name is required")
                } else if (!(lobbySetupComponent.playerName.value.all { it.isLetterOrDigit() })) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Player name should only have letters or digits")
                } else if (lobbySetupComponent.playerName.value.length > 255) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Max length of player name is 255")
                }

                if (lobbySetupComponent.lobbyName.value.isEmpty()) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Lobby name is required")
                } else if (!(lobbySetupComponent.lobbyName.value.all { it.isLetterOrDigit() })) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Lobby name should only have letters or digits")
                } else if (lobbySetupComponent.lobbyName.value.length > 255) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Max length of lobby name is 255")
                }

                val supposedStartingFunds = lobbySetupComponent.startingFunds.value.toIntOrNull()
                if (lobbySetupComponent.startingFunds.value.isEmpty()) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Starting funds are required")
                } else if (supposedStartingFunds == null || supposedStartingFunds <= 0) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Starting funds should be a number between 0 and ${Int.MAX_VALUE}")
                }

                if (valuesCheck) {
                    lobbySetupComponent.onHostingStart(
                        lobbySetupComponent.playerName.value,
                        lobbySetupComponent.lobbyName.value,
                        lobbySetupComponent.startingFunds.value.toInt()
                    )
                }
            },
            content = { Text(text = "Start hosting game") },
        )
    }
}