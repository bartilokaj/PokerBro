package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.blokaj.pokerbro.ui.screens.components.ServerStartingComponent

@Composable
fun ServerStartingScreen(serverStartingComponent: ServerStartingComponent) {
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Sending udp broadcast", style = MaterialTheme.typography.headlineLarge)
    }
}