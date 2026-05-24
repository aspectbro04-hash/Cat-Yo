package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.GamePhase
import com.example.game.GameViewModel

@Composable
fun LobbyScreen(viewModel: GameViewModel, onGameStarted: () -> Unit) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    
    LaunchedEffect(state.phase) {
        if(state.phase != GamePhase.LOBBY) {
            onGameStarted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Room ID: ${state.roomId}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${state.players.size} Players Connected", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(state.players) { player ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(player.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
                        if(player.isHost) {
                            Text("HOST", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        if (viewModel.isHost) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state.players.size >= 1 // Need min players usually, setting 1 for test
            ) {
                Text("Start Game")
            }
        } else {
            Text("Waiting for host...", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
