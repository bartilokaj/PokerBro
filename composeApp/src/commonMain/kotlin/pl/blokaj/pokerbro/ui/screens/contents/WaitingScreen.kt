package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import pl.blokaj.pokerbro.backend.client.ConnectionState
import pl.blokaj.pokerbro.ui.screens.components.WaitingComponent

@Composable
fun WaitingScreen(
    waitingComponent: WaitingComponent
) {
    var dotsCount by remember { mutableStateOf(0) }
    val connectionState = waitingComponent.connectionState.collectAsState()

    LaunchedEffect(connectionState) {
        when(connectionState.value) {
            ConnectionState.CONNECTED -> waitingComponent.onWebsocketSuccess()
            ConnectionState.FAILED -> waitingComponent.onWebsocketFailure()
            ConnectionState.DISCONNECTED -> waitingComponent.onWebsocketFailure()
            else -> {}
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        while(isActive) {
            delay(750)
            if (dotsCount == 3) dotsCount = 0
            else dotsCount += 1
        }
    })

    Column(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(30.dp)
    ) {
        Text(text = "Waiting for host to start server" + ".".repeat(dotsCount), style = MaterialTheme.typography.headlineMedium)
    }
}