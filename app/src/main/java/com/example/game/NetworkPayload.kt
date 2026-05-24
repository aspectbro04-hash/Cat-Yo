package com.example.game

import kotlinx.serialization.Serializable

@Serializable
enum class ActionType {
    JOIN_ROOM,
    STATE_UPDATE,
    CHAT_MESSAGE,
    VOTE,
    NIGHT_ACTION
}

@Serializable
data class NetworkPayload(
    val type: ActionType,
    val senderId: String,
    val data: String // JSON string depending on ActionType
)

@Serializable
data class JoinRoomData(
    val playerName: String
)

@Serializable
data class ActionData(
    val targetId: String
)
