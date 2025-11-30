package pl.blokaj.pokerbro.ui.screens.contents


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pl.blokaj.pokerbro.ui.screens.components.HostingComponent
import pl.blokaj.pokerbro.ui.items.contents.ListContent

@Composable
fun HostingScreen(
    hostingComponent: HostingComponent
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Hosting screen", style = MaterialTheme.typography.headlineLarge)
    }
}