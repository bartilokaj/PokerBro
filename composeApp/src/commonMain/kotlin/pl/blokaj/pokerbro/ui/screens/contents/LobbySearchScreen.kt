package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.blokaj.pokerbro.ui.items.contents.ConfirmationDialog
import pl.blokaj.pokerbro.ui.items.contents.ListContent
import pl.blokaj.pokerbro.ui.items.contents.TopBarBack
import pl.blokaj.pokerbro.ui.screens.components.LobbySearchComponent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LobbySearchScreen(
    lobbySearchComponent: LobbySearchComponent
) {
    val selectedLobby by lobbySearchComponent.selectedLobby.collectAsState()
    Scaffold(
        topBar = { TopBarBack(lobbySearchComponent.onBack) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
}