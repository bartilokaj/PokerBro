package pl.blokaj.pokerbro

import androidx.compose.runtime.*
import com.arkivanov.decompose.ComponentContext
import pl.blokaj.pokerbro.ui.screens.components.MainFlowComponent
import pl.blokaj.pokerbro.ui.screens.components.RootComponent
import pl.blokaj.pokerbro.ui.screens.contents.AppContent
import pl.blokaj.pokerbro.ui.theme.AppTheme

@Composable
fun App(
    rootComponent: RootComponent
) {
    AppTheme {
        AppContent(rootComponent)
    }
}