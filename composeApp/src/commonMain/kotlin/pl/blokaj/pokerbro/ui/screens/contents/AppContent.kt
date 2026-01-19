package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.items.contents.ErrorPopup
import pl.blokaj.pokerbro.ui.screens.components.RootComponent

@Composable
fun AppContent(rootComponent: RootComponent) {
    val childStack by rootComponent.childStack.subscribeAsState()
    val errors by rootComponent.errors.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            StageNavigation(childStack)

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    errors.forEach { error ->
                        ErrorPopup(
                            message = error,
                            onDismiss = { error ->
                                rootComponent.removeError(error)
                            }
                        )
                    }
                }
            }
        }
    }
}