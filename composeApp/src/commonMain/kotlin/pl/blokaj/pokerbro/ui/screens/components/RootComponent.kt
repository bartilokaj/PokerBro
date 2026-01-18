package pl.blokaj.pokerbro.ui.screens.components

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.backend.client.ClientConnectionManager
import pl.blokaj.pokerbro.backend.host.HostingManager
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.ui.screens.components.RootComponent.StageChild.*


class RootComponent(
    componentContext: ComponentContext,
    private val lanNetworkManager: LanNetworkManager
): ComponentContext by componentContext {
    private var stageStack = StackNavigation<Stage>()
    private val playerName = MutableValue("Player")
    private val lobbyName = MutableValue("Lobby")
    private val startingFunds = MutableValue(1)
    private val playerPicturePath = MutableValue("")
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val clientConnectionManager = ClientConnectionManager(lanNetworkManager, { addError(it)} )
    private val hostingManager = HostingManager(lanNetworkManager)
    private val log = Logger.withTag("Root")
    private val _errors = MutableStateFlow<List<String>>(emptyList())
    val errors: StateFlow<List<String>> = _errors

    private lateinit var playerData: PlayerData
    private val players = MutableStateFlow<LinkedHashMap<Int, PlayerData>>(LinkedHashMap())

    val childStack = componentContext.childStack(
        source = stageStack,
        serializer = null,
        initialStack = { listOf(Stage.Flow) },
        childFactory = { config: Stage, context: ComponentContext ->
            when (config) {
                Stage.Flow -> Flow(MainFlowComponent(
                    componentContext = context,
                    onLobbySearch = { playerName: String ->
                        onSearchStarted(playerName)
                    },
                    onHostingStart = { playerName: String, lobbyName: String, startingFunds: Int ->
                        onHostingStarted(playerName, lobbyName, startingFunds)
                    },
                    initialPlayerName = this.playerName,
                    initialPlayerPicturePath = playerPicturePath,
                    initialLobbyName = lobbyName,
                    initialStartingFunds = startingFunds,
                    onWrongInput = { reason ->
                        addError(reason)
                    }
                ))
                Stage.LobbySearch -> LobbySearch(
                    LobbySearchComponent(
                        context,
                        clientConnectionManager,
                        onLobbyFound = { lobbyPair ->
                            onLobbyFound(lobbyPair)
                        }
                    )
                )
                Stage.ServerStarting -> ServerStarting(
                    HostingStartedComponent(
                        context,
                        hostingManager.getPlayersFlow()
                    )
                )
                Stage.Game -> Game(
                    GameComponent(
                        context
                    )
                )
                Stage.Waiting -> Waiting(
                    WaitingComponent(
                        context,
                        clientConnectionManager.connectionState,
                        onWebsocketFailure = {
                            stageStack.pop()
                        },
                        onWebsocketSuccess = {
                            stageStack.replaceAll(Stage.Waiting)
                        }
                    )
                )
            }
        }
    )

    sealed interface Stage {
        object Flow: Stage
        object LobbySearch: Stage
        object ServerStarting: Stage
        object Waiting: Stage
        object Game: Stage
    }

    sealed interface StageChild {
        class Flow(val component: MainFlowComponent) : StageChild
        class LobbySearch(val component: LobbySearchComponent) : StageChild
        class ServerStarting(val component: HostingStartedComponent) : StageChild
        class Waiting(val component: WaitingComponent) : StageChild
        class Game(val component: GameComponent) : StageChild
    }



    fun onSearchStarted(playerName: String) {
        this.playerName.value = playerName
        scope.launch {
            lanNetworkManager.ensureGranted()
        }
        clientConnectionManager.startSearchingJob()
        stageStack.bringToFront(Stage.LobbySearch)
    }

    fun onHostingStarted(
        playerName: String,
        lobbyName: String,
        startingFunds: Int
    ) {
        scope.launch {
            lanNetworkManager.ensureGranted()
        }
        this.playerName.value = playerName
        this.lobbyName.value = lobbyName
        this.startingFunds.value = startingFunds
        hostingManager.startHosting(
            lobbyName,
            startingFunds,
            playerName
        )
        stageStack.bringToFront(Stage.ServerStarting)
    }

    fun onLobbyFound(lobbyPair: Pair<Int, String>) {
        playerData = PlayerData(
            -1,
            playerName.value,
            -1
        )
        stageStack.bringToFront(Stage.Waiting)
        clientConnectionManager.startWebSocketJob(
            lobbyPair,
            playerData,
            players
        )
    }


    private fun addError(error: String) {
        _errors.update { currentList ->
            currentList + error
        }
        scope.launch {
            delay(5000)
            _errors.update { currentList ->
                currentList - error
            }
        }
    }
    fun removeError(error: String) {
        _errors.update { currentList ->
            currentList - error
        }
    }
}
