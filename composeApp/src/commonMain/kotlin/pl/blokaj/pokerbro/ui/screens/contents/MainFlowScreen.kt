package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { flowComponent.goToHome() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { flowComponent.goToJoining() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Join"
                        )
                    },
                    label = { Text("Join game") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { flowComponent.goToHosting() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Host"
                        )
                    },
                    label = { Text("Host game") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Children(stack = flowComponent.flowChildStack) { child ->
                when (val instance = child.instance) {
                    is MainFlowComponent.FlowChild.Home ->
                        HomeScreen(instance.component)

                    is MainFlowComponent.FlowChild.Joining ->
                        JoiningScreen(instance.component)

                    is MainFlowComponent.FlowChild.Hosting ->
                        HostingScreen(instance.component)
                }
            }
        }
    }
}