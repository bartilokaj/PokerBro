package pl.blokaj.pokerbro.backend.client

import pl.blokaj.pokerbro.shared.PlayerData

data class GameState(
    val players: List<PlayerData>,
    val playerData: PlayerData,
    val pot: Int,
    val gameLogs: List<String>
)