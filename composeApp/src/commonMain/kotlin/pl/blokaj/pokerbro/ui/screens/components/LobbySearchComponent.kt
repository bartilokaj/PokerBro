package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeText
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.backend.client.ktor.Lobby
import pl.blokaj.pokerbro.backend.client.ktor.startGameSearching
import pl.blokaj.pokerbro.ui.items.components.FlowListComponent
import pl.blokaj.pokerbro.ui.items.interfaces.ListComponent
import kotlin.time.Duration.Companion.seconds

class LobbySearchComponent(
    componentContext: ComponentContext,
    lanNetworkManager: LanNetworkManager
): ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val lobbyFlow = MutableSharedFlow<Lobby>(replay = 100)
    private lateinit var searchingJob: Job

    init {
        scope.startGameSearching(lanNetworkManager, lobbyFlow)
    }

    val listComponent = FlowListComponent<Lobby>(
        componentContext = childContext("lobby list"),
        flow = lobbyFlow,
        elementClicked = { println(it.toString()) }
    )
}