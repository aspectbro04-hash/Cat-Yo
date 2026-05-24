package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.ConnectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val connectionManager = ConnectionManager(application)
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    var myPlayerId: String = UUID.randomUUID().toString()
        private set
        
    var myName: String = "Player"
    var isHost: Boolean = false
        private set

    init {
        viewModelScope.launch {
            connectionManager.payloads.collect { payload ->
                handlePayload(payload)
            }
        }
    }

    fun hostGame(playerName: String) {
        myName = playerName
        isHost = true
        connectionManager.startHost()
        
        val initialState = GameState(
            roomId = UUID.randomUUID().toString().take(6),
            players = listOf(Player(id = myPlayerId, name = playerName, isHost = true))
        )
        _gameState.value = initialState
        broadcastState()
    }
    
    fun joinGame(playerName: String) {
        myName = playerName
        connectionManager.discoverAndConnect(playerName, myPlayerId)
    }

    private fun handlePayload(payload: NetworkPayload) {
        when(payload.type) {
            ActionType.JOIN_ROOM -> {
                if(isHost) {
                    val joinData = Json.decodeFromString<JoinRoomData>(payload.data)
                    _gameState.update { state -> 
                        val newPlayers = state.players.toMutableList()
                        if(newPlayers.none { it.id == payload.senderId }) {
                            newPlayers.add(Player(payload.senderId, joinData.playerName))
                        }
                        state.copy(players = newPlayers)
                    }
                    broadcastState()
                }
            }
            ActionType.STATE_UPDATE -> {
                val newState = Json.decodeFromString<GameState>(payload.data)
                _gameState.value = newState
            }
            ActionType.NIGHT_ACTION -> {
                // simple mock: just acknowledge
            }
            ActionType.VOTE -> {}
            ActionType.CHAT_MESSAGE -> {}
        }
    }
    
    private fun broadcastState() {
        if(!isHost) return
        val stateJson = Json.encodeToString(_gameState.value)
        val payload = NetworkPayload(ActionType.STATE_UPDATE, myPlayerId, stateJson)
        connectionManager.sendPayload(payload)
    }
    
    fun startGame() {
        if(!isHost) return
        // Assign roles randomly
        val currentPlayers = _gameState.value.players.toMutableList()
        val roles = mutableListOf(Role.MAFIA, Role.DOCTOR, Role.SHERIFF)
        while(roles.size < currentPlayers.size) {
            roles.add(Role.CIVILIAN)
        }
        roles.shuffle()
        
        currentPlayers.forEachIndexed { index, player -> 
            player.role = roles[index]
        }
        
        _gameState.update { it.copy(
            players = currentPlayers,
            phase = GamePhase.NIGHT
        ) }
        broadcastState()
    }
    
    fun submitNightAction(targetId: String) {
        val payload = NetworkPayload(
            ActionType.NIGHT_ACTION,
            myPlayerId,
            Json.encodeToString(ActionData(targetId))
        )
        connectionManager.sendPayload(payload)
    }
    
    fun nextPhase() {
        if(!isHost) return
        val next = when(_gameState.value.phase) {
            GamePhase.LOBBY -> GamePhase.NIGHT
            GamePhase.NIGHT -> GamePhase.DAY_DISCUSSION
            GamePhase.DAY_DISCUSSION -> GamePhase.DAY_VOTE
            GamePhase.DAY_VOTE -> GamePhase.NIGHT
            else -> GamePhase.LOBBY
        }
        _gameState.update { it.copy(phase = next) }
        broadcastState()
    }
}
