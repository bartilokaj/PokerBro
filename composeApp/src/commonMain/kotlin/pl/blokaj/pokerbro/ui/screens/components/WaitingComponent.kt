package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import pl.blokaj.pokerbro.backend.client.ConnectionState

class WaitingComponent(
    componentContext: ComponentContext,
    val connectionState: StateFlow<ConnectionState>,
    val onWebsocketFailure: () -> Unit,
    val onWebsocketSuccess: () -> Unit
): ComponentContext by componentContext {
}