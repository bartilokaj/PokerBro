package pl.blokaj.pokerbro.backend.client

import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.Lobby
import pl.blokaj.pokerbro.utility.ThreadSafeMap

class ClientConnectionManager(
    private val lanNetworkManager: LanNetworkManager,
    private val onError: (message: String) -> Unit
) {
    private val lobbyFlow = MutableSharedFlow<Pair<Int, String>>(extraBufferCapacity = 10)
    private lateinit var searchingJob: Job
    private lateinit var socketJob: Job
    private val lobbyMap: ThreadSafeMap<Int, Lobby> = ThreadSafeMap(HashMap())
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val log = Logger.withTag("ClientConnectionManager")
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.PRECONNECTION)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    fun startSearchingJob() {
        log.i { "Starting search..." }
        searchingJob = ClientUdpDiscoveryService.run {
            scope.startGameSearching(
                lanNetworkManager,
                lobbyMap,
                lobbyFlow
            )
        }
    }

    fun startWebSocketJob(
        lobbyPair: Pair<Int, String>,
        playerName: String
    ) {
        scope.launch {
            val lobby = lobbyMap.get(lobbyPair.first)
            if (lobby == null) TODO()
            else {
                log.i { "Trying to connect to lobby ${lobby}..." }
                socketJob = ClientWebsocket.run {
                    scope.connectToGameServer(
                        lobby,
                        playerName,
                        _connectionState,
                        onError
                    )
                }
            }
        }
    }


    fun getLobbyFlow(): MutableSharedFlow<Pair<Int, String>> {
        return lobbyFlow
    }

}