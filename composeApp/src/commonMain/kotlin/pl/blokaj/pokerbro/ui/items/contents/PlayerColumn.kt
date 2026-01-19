package pl.blokaj.pokerbro.ui.items.contents

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import pl.blokaj.pokerbro.shared.PlayerData

@Composable
fun PlayerColumn(
    playerFlow: StateFlow<List<PlayerData>>,
    topText: String,
    modifier: Modifier = Modifier
) {
    val players by playerFlow.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(players.size) {
        if (players.isNotEmpty()) {
            listState.animateScrollToItem(players.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        reverseLayout = false, // top-to-bottom
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        stickyHeader {
            StickyElement(topText)
        }
        items(players) { player ->
            PlayerItem(player)
        }
    }
}