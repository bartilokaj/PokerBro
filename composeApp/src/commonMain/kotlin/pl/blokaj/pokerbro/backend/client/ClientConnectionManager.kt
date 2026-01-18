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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.Lobby
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.utility.ThreadSafeMap

class ClientConnectionManager(
    private val lanNetworkManager: LanNetworkManager,
    private val onError: (message: String) -> Unit
) {
    private val lobbyFlow = MutableStateFlow<List<Pair<Int, String>>>(emptyList())
    private lateinit var searchingJob: Job
    private lateinit var socketJob: Job
    private val lobbyMap: ThreadSafeMap<Int, Lobby> = ThreadSafeMap(HashMap())
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val log = Logger.withTag("ClientConnectionManager")
    private val _connectionState = MutableStateFlow(ConnectionState.PRECONNECTION)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    fun startSearchingJob() {
        log.i { "Starting search..." }
        if (!::searchingJob.isInitialized || !searchingJob.isActive) {
            searchingJob = ClientUdpDiscoveryService.run {
                scope.startGameSearching(
                    lanNetworkManager,
                    lobbyMap,
                    lobbyFlow
                )
            }
        }
    }

    fun startWebSocketJob(
        lobbyPair: Pair<Int, String>,
        playerData: PlayerData,
        players: MutableStateFlow<LinkedHashMap<Int, PlayerData>>
    ) {
        scope.launch {
            val lobby = lobbyMap.get(lobbyPair.first)
            if (lobby == null) {
                log.i { "Failed to find match to $lobbyPair" }
                _connectionState.value = ConnectionState.FAILED
                onError("Lobby ${lobbyPair.second} is no longer available")
                removeLobby(lobbyPair)
            } else {
                log.i { "Trying to connect to lobby ${lobby}..." }
                socketJob = ClientWebsocket.run {
                    scope.connectToGameServer(
                        lobby,
                        _connectionState,
                        playerData,
                        players,
                        onError = { message, lobby ->
                            scope.launch {
                                removeLobby(Pair(lobby.id, "${lobby.lobbyName} by ${lobby.hostName}"))
                            }
                            onError(message)
                        }
                    )
                }
            }
        }
    }

    suspend fun removeLobby(lobbyPair: Pair<Int, String>) {
        val deletedLobby = lobbyMap.remove(lobbyPair.first)
        lobbyFlow.update { it - lobbyPair }
        if(deletedLobby != null) ClientUdpDiscoveryService.removeLobby(deletedLobby)
    }


    fun getLobbyFlow(): StateFlow<List<Pair<Int, String>>> {
        return lobbyFlow.asStateFlow()
    }

}