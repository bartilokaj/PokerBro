package pl.blokaj.pokerbro.backend.host

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.PlayerData
import kotlin.random.Random

class HostingManager(
    private val lanNetworkManager: LanNetworkManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gamePort = Random.nextInt(50000, 60000)
    private val serverWebsocket = ServerWebsocket(scope, gamePort)

    fun startHosting(
        lobbyName: String,
        startingFunds: Int,
        host: String
    ) {
        ServerUdpDiscoveryService.run {
            scope.broadcastLobby(
                lanNetworkManager,
                gamePort,
                lobbyName,
                host
            )
        }
        serverWebsocket.start(startingFunds)
    }

    fun startGame() {
        serverWebsocket.startGame()
    }

    fun getPlayersFlow(): MutableStateFlow<List<PlayerData>> {
        return serverWebsocket.getPlayersFlow()
    }

}