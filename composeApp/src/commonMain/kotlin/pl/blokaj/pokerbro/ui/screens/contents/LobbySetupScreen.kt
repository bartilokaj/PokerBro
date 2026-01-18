package pl.blokaj.pokerbro.ui.screens.contents


import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.screens.components.LobbySetupComponent
import pl.blokaj.pokerbro.ui.items.contents.ProfilePicture
import pl.blokaj.pokerbro.ui.items.contents.TextInputField

@Composable
fun HostingScreen(
    lobbySetupComponent: LobbySetupComponent
) {
    var playerName by remember { mutableStateOf(lobbySetupComponent.initialPlayerName) }
    var lobbyName by remember { mutableStateOf(lobbySetupComponent.initialLobbyName) }
    var startingFunds by remember { mutableStateOf(lobbySetupComponent.initialStartingFunds.toString()) }
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
                    text = "PLAYER NAME:",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                )
                TextInputField(
                    value = playerName,
                    onValueChange = { playerName = it },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOBBY NAME:",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                )
                TextInputField(
                    value = lobbyName,
                    onValueChange = { lobbyName = it },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STARTING FUNDS:",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                )
                TextInputField(
                    value = startingFunds,
                    onValueChange = { startingFunds = it },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                var valuesCheck = true
                if (playerName.isEmpty()) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Player name is required")
                } else if (!(playerName.all { it.isLetterOrDigit() })) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Player name should only have letters or digits")
                } else if (playerName.length > 255) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Max length of player name is 255")
                }

                if (lobbyName.isEmpty()) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Lobby name is required")
                } else if (!(lobbyName.all { it.isLetterOrDigit() })) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Lobby name should only have letters or digits")
                } else if (lobbyName.length > 255) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Max length of lobby name is 255")
                }

                val supposedStartingFunds = startingFunds.toIntOrNull()
                if (startingFunds.isEmpty()) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Starting funds are required")
                } else if (supposedStartingFunds == null || supposedStartingFunds <= 0) {
                    valuesCheck = false
                    lobbySetupComponent.onWrongInput("Starting funds should be a number between 0 and ${Int.MAX_VALUE}")
                }

                if (valuesCheck) {
                    lobbySetupComponent.onHostingStart(playerName, lobbyName, startingFunds.toInt())
                }
            },
            content = { Text(text = "Start hosting game") },
        )
    }
}