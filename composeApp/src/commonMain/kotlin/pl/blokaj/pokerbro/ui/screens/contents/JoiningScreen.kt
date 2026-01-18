package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.screens.components.JoiningComponent
import pl.blokaj.pokerbro.ui.items.contents.ProfilePicture
import pl.blokaj.pokerbro.ui.items.contents.TextInputField

@Composable
fun JoiningScreen(joiningComponent: JoiningComponent) {
    val playerName by joiningComponent.localName.subscribeAsState()

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

        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLAYER NAME",
                modifier = Modifier
                    .fillMaxWidth(0.3f)
            )
            TextInputField(
                value = playerName,
                onValueChange = { joiningComponent.localName.value = it },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (playerName.all { it.isLetterOrDigit() }) {
                    joiningComponent.onLobbySearch(playerName)
                } else {
                    joiningComponent.onWrongInput("Player name should only have letters or digits")
                }
            },
            content = { Text(text = "Start game search") },
        )
    }
}