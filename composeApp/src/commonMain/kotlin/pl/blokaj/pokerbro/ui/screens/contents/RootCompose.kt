package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.screens.components.FlowChild
import pl.blokaj.pokerbro.ui.screens.components.FlowScreen
import pl.blokaj.pokerbro.ui.screens.components.RootComponent
import pl.blokaj.pokerbro.ui.screens.components.Stage
import pl.blokaj.pokerbro.ui.screens.components.StageChild

@Composable
fun RootCompose(rootComponent: RootComponent) {
    Children(stack = rootComponent.childStack) { child ->
        when (val instance = child.instance) {
            is StageChild.Flow -> MainFlow(instance.component)
            is StageChild.LobbySearch -> LobbySearchScreen(instance.component)
            is StageChild.ServerStarting -> ServerStartingScreen(instance.component)
            is StageChild.Game -> GameScreen(instance.component)
        }
    }
}