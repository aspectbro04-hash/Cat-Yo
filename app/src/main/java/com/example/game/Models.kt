package com.example.game

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    MAFIA,
    DON,
    SHERIFF,
    DOCTOR,
    CIVILIAN,
    MANIAC,
    LOVER,
    SNIPER
}

@Serializable
enum class GamePhase {
    LOBBY,
    NIGHT,
    DAY_DISCUSSION,
    DAY_VOTE,
    GAME_OVER
}

@Serializable
data class Player(
    val id: String,
    val name: String,
    var role: Role? = null,
    var isAlive: Boolean = true,
    var isHost: Boolean = false
)

@Serializable
data class GameState(
    val roomId: String = "",
    val phase: GamePhase = GamePhase.LOBBY,
    val players: List<Player> = emptyList(),
    val timerSeconds: Int = 0,
    val winnerSide: String? = null
)
