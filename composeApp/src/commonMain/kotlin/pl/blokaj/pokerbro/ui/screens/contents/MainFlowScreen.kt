package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import pl.blokaj.pokerbro.ui.screens.components.MainFlowComponent
import pl.blokaj.pokerbro.ui.items.contents.BottomBarIcon


@Composable
fun MainFlowScreen(
    flowComponent: MainFlowComponent
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomBarIcon(
                        onClick = { flowComponent.goToHome() },
                        icon = Icons.Default.Home,
                        contentDescription = "Home",
                        text = "Home",
                        selected = false
                    )
                    BottomBarIcon(
                        onClick = { flowComponent.goToJoining() },
                        icon = Icons.Default.Groups,
                        contentDescription = "Join",
                        text = "Join game",
                        selected = false
                    )
                    BottomBarIcon(
                        onClick = { flowComponent.goToHosting() },
                        icon = Icons.Default.AddCircle,
                        contentDescription = "Host",
                        text = "Host game",
                        selected = false
                    )
                    BottomBarIcon(
                        onClick = {},
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        text = "Settings",
                        selected = false
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Children(stack = flowComponent.flowChildStack) { child ->
                when (val instance = child.instance) {
                    is MainFlowComponent.FlowChild.Home -> HomeScreen(instance.component)
                    is MainFlowComponent.FlowChild.Joining -> JoiningScreen(instance.component)
                    is MainFlowComponent.FlowChild.Hosting -> HostingScreen(instance.component)
                }
            }
        }
    }
}