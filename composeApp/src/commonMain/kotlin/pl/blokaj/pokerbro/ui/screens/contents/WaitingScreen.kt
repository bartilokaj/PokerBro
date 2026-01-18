package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.io.files.Path
import pl.blokaj.pokerbro.backend.client.ConnectionState
import pl.blokaj.pokerbro.ui.items.contents.WaitingCircle
import pl.blokaj.pokerbro.ui.items.contents.WaitingText
import pl.blokaj.pokerbro.ui.screens.components.WaitingComponent

@Composable
fun WaitingScreen(
    waitingComponent: WaitingComponent
) {
    val connectionState = waitingComponent.connectionState.collectAsState()
    val connectionEstablished = MutableValue<Boolean>(false)

    LaunchedEffect(connectionState) {
        when(connectionState.value) {
            ConnectionState.CONNECTED -> {
                connectionEstablished.value = true
                waitingComponent.onWebsocketSuccess()
            }
            ConnectionState.FAILED, ConnectionState.DISCONNECTED -> waitingComponent.onWebsocketFailure()
            else -> {}
        }
    }
    if (connectionEstablished.value) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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