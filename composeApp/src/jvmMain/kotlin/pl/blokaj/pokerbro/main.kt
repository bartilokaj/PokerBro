package pl.blokaj.pokerbro

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import pl.blokaj.pokerbro.backend.LanNetworkManager
import pl.blokaj.pokerbro.ui.screens.components.RootComponent

fun main() = application {
    val lifecycle = LifecycleRegistry()
    val log = Logger.withTag("DesktopMain")

    val lanNetworkManager = LanNetworkManager()
    val rootComponent = RootComponent(
        DefaultComponentContext(lifecycle),
        lanNetworkManager
    )

    Window(
        onCloseRequest = {
            lifecycle.destroy()   // clean Decompose
            exitApplication()
        },
        title = "PokerBro"
    ) {
        App(rootComponent)
    }
}
