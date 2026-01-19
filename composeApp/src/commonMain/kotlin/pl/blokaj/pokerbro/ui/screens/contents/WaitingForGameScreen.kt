package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.blokaj.pokerbro.backend.client.ConnectionState
import pl.blokaj.pokerbro.ui.items.contents.TopBarBack
import pl.blokaj.pokerbro.ui.items.contents.WaitingCircle
import pl.blokaj.pokerbro.ui.items.contents.WaitingText
import pl.blokaj.pokerbro.ui.screens.components.WaitingForGameComponent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WaitingScreen(
    waitingForGameComponent: WaitingForGameComponent
) {
    val connectionState = waitingForGameComponent.connectionState.collectAsState()
    val connectionEstablished = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBarBack(waitingForGameComponent.onBack) }
    ) {

        LaunchedEffect(connectionState.value) {
            when(connectionState.value) {
                ConnectionState.CONNECTED -> {
                    connectionEstablished.value = true
                    waitingForGameComponent.onWebsocketSuccess()
                }
                ConnectionState.GAME -> {
                    waitingForGameComponent.onGameStarted()
                }
                ConnectionState.FAILED, ConnectionState.DISCONNECTED -> {
                    waitingForGameComponent.onWebsocketFailure()
                }
                else -> {}
            }
        }
        if (connectionEstablished.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                WaitingText("Waiting for host to start server")
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                WaitingCircle()
            }

        }
    }
}