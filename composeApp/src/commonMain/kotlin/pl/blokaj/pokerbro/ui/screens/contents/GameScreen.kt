package pl.blokaj.pokerbro.ui.screens.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.items.contents.LabeledInputRow
import pl.blokaj.pokerbro.ui.items.contents.LogColumn
import pl.blokaj.pokerbro.ui.items.contents.PlayerColumn
import pl.blokaj.pokerbro.ui.screens.components.GameComponent

@Composable
fun GameScreen(gameComponent: GameComponent) {
    val betInputValue by gameComponent.currentBet.subscribeAsState()
    val takePotInputValue by gameComponent.currentTakePot.subscribeAsState()
    val currentPlayerData by gameComponent.playerDataState.collectAsState()
    val currentPot by gameComponent.potState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 60.dp, horizontal = 15.dp)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.onBackground)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                LogColumn(
                    gameComponent.gameLogsState,
                    topText = "GAME LOG",
                    modifier = Modifier
                        .weight(0.90f)
                )
                Text(
                    text = "Current pot: $currentPot",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.onBackground)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                PlayerColumn(
                    gameComponent.playersState,
                    topText = "PLAYERS"
                )
            }
        }

        Spacer(Modifier.height(20.dp))


        // Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(0.9f)
            ) {
                LabeledInputRow(
                    "Bet",
                    betInputValue,
                    onValueChange = {
                        gameComponent.currentBet.value = it
                    },
                    rightText = "/${currentPlayerData.funds}"
                )
                Button(
                    onClick = {
                        val bet = betInputValue.toIntOrNull()
                        if (betInputValue.isNotEmpty() && bet != null && bet > 0) {
                            gameComponent.onPlaceBet(bet)
                        }
                        else {
                            gameComponent.onError("Bet must be a positive number")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Place bet")
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(0.9f)
            ) {
                LabeledInputRow(
                    "Take from pot",
                    takePotInputValue,
                    onValueChange = {
                        gameComponent.currentTakePot.value = it
                    },
                    rightText = "/${currentPot}"
                )
                OutlinedButton(
                    onClick = {
                        val amount = betInputValue.toIntOrNull()
                        if (betInputValue.isNotEmpty() && amount != null && amount > 0) {
                            gameComponent.onTakePot(amount)
                        }
                        else {
                            gameComponent.onError("Amount must be a positive number")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take pot")
                }
            }
        }

    }
}