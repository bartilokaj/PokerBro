package pl.blokaj.pokerbro.ui.items.contents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import pl.blokaj.pokerbro.ui.items.interfaces.ListComponent

@Composable
fun <T> ListContent (
    component: ListComponent<T>,
    modifier: Modifier = Modifier
) {
    val state by component.model.subscribeAsState()
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = modifier) {
        items(state) { element ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { component.onElementClicked(element) }
                    .padding(16.dp)
            ) {
                Text(text = element.toString())
            }
        }
    }
}