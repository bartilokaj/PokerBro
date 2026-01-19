package pl.blokaj.pokerbro.backend.host

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.PlayerData

class HostingManager(
    private val lanNetworkManager: LanNetworkManager,
    private val gamePort: Int,
    private val onError: (message: String) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val log = Logger.withTag("HostingManager")
    private val serverState = MutableStateFlow(ServerState.STOPPED)
    private val serverWebsocket = ServerWebsocket(
        scope,
        gamePort,
        onFailure = {
            onError("Failed to start server")
            stopHosting()
        }
    )
    private lateinit var broadcastJob: Job

    fun startHosting(
        lobbyName: String,
        startingFunds: Int,
        host: String
    ) {
        broadcastJob = ServerUdpDiscoveryService.run {
            scope.broadcastLobby(
                lanNetworkManager,
                gamePort,
                lobbyName,
                host,
                onFailure = {
                    onError("Failed to start server")
                    stopHosting()
                }
            )
        }
        serverWebsocket.start(
            serverState,
            startingFunds
        )
    }

    fun stopHosting() {
        broadcastJob.cancel()
        serverWebsocket.stop()
        serverState.value = ServerState.STOPPED
    }

    fun stopBroadcastJob() {
        broadcastJob.cancel()
    }

    fun startGame() {
        serverWebsocket.startGame()
    }

    fun getPlayersFlow(): StateFlow<List<PlayerData>> {
        return serverWebsocket.getPlayersFlow()
    }

    fun getServerState(): MutableStateFlow<ServerState> {
        return serverState
    }

}