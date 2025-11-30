package pl.blokaj.pokerbro.shared

import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val name: String,
    val imageString: String
)
