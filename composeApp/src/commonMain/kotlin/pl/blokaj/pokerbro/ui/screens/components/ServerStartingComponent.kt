package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.backend.client.ktor.Lobby
import pl.blokaj.pokerbro.backend.client.ktor.startGameSearching
import pl.blokaj.pokerbro.backend.host.ktor.startServer

class ServerStartingComponent(
    componentContext: ComponentContext,
    lanNetworkManager: LanNetworkManager
): ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startServer(lanNetworkManager, "placeholder", scope)
    }
}