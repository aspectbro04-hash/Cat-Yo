package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.GamePhase
import com.example.game.GameViewModel
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle

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
            .padding(24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("LOBBY ID: ${state.roomId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("WAITING", fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
            }
            Text("${state.players.size} PLAYERS SECURED", fontSize = 10.sp, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.secondary)
        }
        
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(state.players) { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if(player.id == viewModel.myPlayerId) MaterialTheme.colorScheme.primary else BorderMedium, RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(player.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    if(player.isHost) {
                        Text("HOST", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (viewModel.isHost) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = state.players.size >= 1 // allow 1 for test
            ) {
                Text("INITIATE MATCH", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, BorderMedium, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("AWAITING HOST...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

