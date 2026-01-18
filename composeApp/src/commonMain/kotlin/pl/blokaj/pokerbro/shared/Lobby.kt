package pl.blokaj.pokerbro.shared

data class Lobby(
    val id: Int,
    val lobbyName: String,
    val hostName: String,
    val ip: String,
    val port: Int
)
