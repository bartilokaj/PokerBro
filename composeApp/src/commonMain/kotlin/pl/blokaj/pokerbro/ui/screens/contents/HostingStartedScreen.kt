package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import pl.blokaj.pokerbro.ui.items.contents.ListContent
import pl.blokaj.pokerbro.ui.items.contents.WaitingText
import pl.blokaj.pokerbro.ui.screens.components.HostingStartedComponent

@Composable
fun HostingStartedScreen(hostingStartedComponent: HostingStartedComponent) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .padding(vertical = 50.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaitingText("Letting players in")
            ListContent(hostingStartedComponent.listComponent)
            Button(
                onClick = {},
                content = { Text("Start game") }
            )
        }
    }
}