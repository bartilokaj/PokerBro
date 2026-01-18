package pl.blokaj.pokerbro.ui.items.contents

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.getValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun WaitingText(
    text: String
) {
    var dotsCount by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = Unit, block = {
        while(isActive) {
            delay(750)
            if (dotsCount == 3) dotsCount = 0
            else dotsCount += 1
        }
    })
    Text(
        text = text + ".".repeat(dotsCount),
        style = MaterialTheme.typography.headlineMedium
    )
}