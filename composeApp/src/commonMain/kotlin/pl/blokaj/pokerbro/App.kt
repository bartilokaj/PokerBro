package pl.blokaj.pokerbro

import androidx.compose.runtime.*
import com.arkivanov.decompose.ComponentContext
import pl.blokaj.pokerbro.ui.screens.components.MainFlowComponent
import pl.blokaj.pokerbro.ui.screens.contents.MainFlow
import pl.blokaj.pokerbro.ui.theme.AppTheme

@Composable
fun App(
    componentContext: ComponentContext
) {
    AppTheme {
        val flowComponent = remember { MainFlowComponent(componentContext) }
        MainFlow(flowComponent)
    }
}