package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.screens.components.JoiningComponent
import pl.blokaj.pokerbro.ui.items.contents.ProfilePicture
import pl.blokaj.pokerbro.ui.items.contents.TextInputField

@Composable
fun JoiningScreen(joiningComponent: JoiningComponent) {
    val playerName = joiningComponent.playerName.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Joining Screen", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))
        ProfilePicture(joiningComponent.profilePictureComponent)

        Spacer(modifier = Modifier.height(16.dp))
        TextInputField(
            value = playerName.value,
            onValueChange = { joiningComponent.setPlayerName(it) },
            labelText = "Player name"
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button()
    }
}