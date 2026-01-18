package pl.blokaj.pokerbro.ui.items.contents

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment


@Composable
fun ErrorPopup(
    message: String,
    onDismiss: (String) -> Unit
    ) {
    Row(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            modifier = Modifier
                .padding(end = 24.dp), // space for X
            color = MaterialTheme.colorScheme.onError
        )

        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close",
            tint = Color.Black,
            modifier = Modifier
                .size(25.dp)
                .clickable { onDismiss(message) }
        )
    }
}
