package pl.blokaj.pokerbro.ui.screens.components

import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.CoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.launch
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.shared.PlayerData
import pl.blokaj.pokerbro.ui.screens.contents.MainFlow


class RootComponent(
    componentContext: ComponentContext,
    private val lanManager: LanNetworkManager
): ComponentContext by componentContext {
    private var stageStack = StackNavigation<Stage>()
    private var playerDetails = PlayerData("", "")
    private var playerPicturePath = ""
    private val scope = coroutineScope()
    val childStack = componentContext.childStack(
        source = stageStack,
        serializer = null,
        initialStack = { listOf(Stage.Flow) },
        childFactory = { config: Stage, context: ComponentContext ->
            when (config) {
                Stage.Flow -> StageChild.Flow(MainFlowComponent(
                    componentContext = context,
                    onLobbySearch = { playerName: String, imgString: String ->
                        onSearchStarted(playerName, imgString)
                    },
                    onHostingStart = { playerName: String, imgString: String ->
                        onHostingStarted(playerName, imgString)
                    },
                    initialPlayerName = playerDetails.name,
                    initialPlayerPicturePath = playerPicturePath
                ))
                Stage.LobbySearch -> StageChild.LobbySearch(LobbySearchComponent(context, lanManager))
                Stage.ServerStarting -> StageChild.ServerStarting(ServerStartingComponent(context, lanManager))
                Stage.Game -> StageChild.Game(GameComponent(context))
            }
        }
    )

    sealed interface Stage {
        object Flow: Stage
        object LobbySearch: Stage
        object ServerStarting: Stage
        object Game: Stage
    }

    sealed interface StageChild {
        class Flow(val component: MainFlowComponent) : StageChild
        class LobbySearch(val component: LobbySearchComponent) : StageChild
        class ServerStarting(val component: ServerStartingComponent) : StageChild
        class Game(val component: GameComponent) : StageChild
    }



    fun onSearchStarted(playerName: String, imageString: String) {
        playerDetails = PlayerData(playerName, imageString)
        scope.launch {
            lanManager.ensureGranted()
        }
        stageStack.bringToFront(Stage.LobbySearch)
    }

    fun onHostingStarted(playerName: String, imageString: String) {
        playerDetails = PlayerData(playerName, imageString)
        scope.launch {
            lanManager.ensureGranted()
        }
        stageStack.bringToFront(Stage.ServerStarting)
    }

}
