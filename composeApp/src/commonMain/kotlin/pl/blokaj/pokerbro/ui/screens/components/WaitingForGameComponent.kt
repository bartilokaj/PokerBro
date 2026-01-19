package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import pl.blokaj.pokerbro.backend.client.ConnectionState

class WaitingForGameComponent(
    componentContext: ComponentContext,
    val connectionState: StateFlow<ConnectionState>,
    val onWebsocketFailure: () -> Unit,
    val onWebsocketSuccess: () -> Unit,
    val onGameStarted: () -> Unit,
    val onBack: () -> Unit
): ComponentContext by componentContext