package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent
import pl.blokaj.pokerbro.ui.items.contents.ConfirmationDialog
import pl.blokaj.pokerbro.ui.items.contents.ListContent
import pl.blokaj.pokerbro.ui.items.contents.ProfilePicture
import pl.blokaj.pokerbro.ui.items.contents.TextInputField
import pl.blokaj.pokerbro.ui.screens.components.JoiningComponent
import pl.blokaj.pokerbro.ui.screens.components.LobbySearchComponent

@Composable
fun LobbySearchScreen(
    lobbySearchComponent: LobbySearchComponent
) {
    val selectedLobby by lobbySearchComponent.selectedLobby.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Found lobbies", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))

            ListContent(lobbySearchComponent.listComponent)
        }

        selectedLobby?.let { lobbyPair ->
            ConfirmationDialog(
                message = "Do you want to join ${lobbySearchComponent.listComponent.toStringFn(lobbyPair)}?",
                onConfirm = { lobbySearchComponent.onLobbyConfirmed(lobbyPair) },
                onDismiss = { lobbySearchComponent.onLobbyDismissed() }
            )

        }
    }
}