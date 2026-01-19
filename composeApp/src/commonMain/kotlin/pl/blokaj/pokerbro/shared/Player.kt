package pl.blokaj.pokerbro.shared

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    var id: Int,
    val name: String,
    var funds: Int
)
