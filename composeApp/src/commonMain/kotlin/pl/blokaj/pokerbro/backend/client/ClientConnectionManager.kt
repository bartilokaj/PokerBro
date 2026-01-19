package pl.blokaj.pokerbro.backend.client

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.Lobby
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
    private val _connectionStateFlow = MutableStateFlow(ConnectionState.PRECONNECTION)
    val connectionStateFlow: StateFlow<ConnectionState> = _connectionStateFlow.asStateFlow()

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

    fun stopSearchingJob() {
        log.i { "Stoping searching for lobby" }
        searchingJob.cancel()
    }

    fun startWebSocketJob(
        lobbyPair: Pair<Int, String>,
        playerName: String
    ) {
        socketJob = scope.launch {
            val lobby = lobbyMap.get(lobbyPair.first)
            if (lobby == null) {
                log.i { "Failed to find match to $lobbyPair" }
                _connectionStateFlow.value = ConnectionState.FAILED
                onError("Lobby ${lobbyPair.second} is no longer available")
                removeLobby(lobbyPair)
            } else {
                log.i { "Trying to connect to lobby ${lobby}..." }
                socketJob = ClientWebsocketManager.run {
                    scope.connectToGameServer(
                        _connectionStateFlow,
                        playerName,
                        lobby,
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

    fun startLocalClient(
        playerName: String,
        lobbyName: String,
        gamePort: Int
    ) {
        val localLobby = Lobby(
            Int.MAX_VALUE,
            lobbyName,
            playerName,
            ip = "127.0.0.1",
            gamePort
        )
        log.i { "Starting local game client..." }
        socketJob = ClientWebsocketManager.run {
            scope.connectToGameServer(
                _connectionStateFlow,
                playerName,
                localLobby,
                onError = { message, lobby ->
                    scope.launch {
                        removeLobby(Pair(lobby.id, "${lobby.lobbyName} by ${lobby.hostName}"))
                    }
                    onError(message)
                }
            )
        }
    }

    fun stopSocketJob() {
        log.i { "Stopping client socket" }
        socketJob.cancel()
    }

    suspend fun removeLobby(lobbyPair: Pair<Int, String>) {
        val deletedLobby = lobbyMap.remove(lobbyPair.first)
        lobbyFlow.update { it - lobbyPair }
        if(deletedLobby != null) ClientUdpDiscoveryService.removeLobby(deletedLobby)
    }


    fun getLobbyFlow(): StateFlow<List<Pair<Int, String>>> {
        return lobbyFlow.asStateFlow()
    }

    fun getGameStateFlow(): StateFlow<GameState> {
        return ClientWebsocketManager.getGameStateFlow()
    }

    fun placeBet(bet: Int) {
        scope.launch {
            ClientWebsocketManager.placeBet(bet)
        }
    }

    fun takePot(amount: Int) {
        scope.launch {
            ClientWebsocketManager.takePot(amount)
        }
    }

}