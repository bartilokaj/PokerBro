package pl.blokaj.pokerbro.ui.items.contents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(labelText) },
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(0.8f),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}
