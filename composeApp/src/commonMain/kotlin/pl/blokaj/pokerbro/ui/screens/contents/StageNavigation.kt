package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import pl.blokaj.pokerbro.ui.screens.components.RootComponent

@Composable
fun StageNavigation(childStack: ChildStack<RootComponent.Stage, RootComponent.StageChild>) {
    Children(stack = childStack) { child ->
        when (val instance = child.instance) {
            is RootComponent.StageChild.Flow -> MainFlowScreen(instance.component)
            is RootComponent.StageChild.LobbySearch -> LobbySearchScreen(instance.component)
            is RootComponent.StageChild.ServerStarting -> HostingStartedScreen(instance.component)
            is RootComponent.StageChild.Game -> GameScreen(instance.component)
            is RootComponent.StageChild.Waiting -> WaitingScreen(instance.component)
        }
    }
}
