package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.blokaj.pokerbro.backend.host.ServerState
import pl.blokaj.pokerbro.ui.items.contents.ListContent
import pl.blokaj.pokerbro.ui.items.contents.TopBarBack
import pl.blokaj.pokerbro.ui.items.contents.WaitingText
import pl.blokaj.pokerbro.ui.screens.components.HostingStartedComponent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HostingStartedScreen(hostingStartedComponent: HostingStartedComponent) {
    val serverState = hostingStartedComponent.serverState.collectAsState()

    Scaffold(
        topBar = { TopBarBack(hostingStartedComponent.onBack) }
    ) {
        LaunchedEffect(serverState.value) {
            when(serverState.value) {
                ServerState.RUNNING -> {}
                ServerState.STOPPED -> hostingStartedComponent.onBack()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .padding(vertical = 50.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WaitingText("Letting players in")
                ListContent(hostingStartedComponent.listComponent)
                Button(
                    onClick = {
                        if (serverState.value != ServerState.RUNNING) {
                            hostingStartedComponent.onError("Server hasn't fully started yet...")
                        } else {
                            hostingStartedComponent.onGameStart()
                        }
                    },
                    content = { Text("Start game") }
                )
            }
        }
    }
}