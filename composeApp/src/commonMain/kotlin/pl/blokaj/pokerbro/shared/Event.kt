package pl.blokaj.pokerbro.shared

import kotlinx.serialization.Serializable

@Serializable
sealed interface EventPayload

@Serializable
data object RegisterPayload: EventPayload

@Serializable
data class ProfilePicturePayload(val imageBytes: ByteArray): EventPayload

@Serializable
data class Event(
    val userName: String,
    val payload: EventPayload
)
