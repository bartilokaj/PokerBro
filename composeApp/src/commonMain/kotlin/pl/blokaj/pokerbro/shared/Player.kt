package pl.blokaj.pokerbro.shared

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    var id: Int,
//    val imageString: String,
    val name: String,
    val funds: Int
)
