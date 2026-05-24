package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.GamePhase
import com.example.game.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel, onGameEnded: () -> Unit) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val myPlayer = state.players.find { it.id == viewModel.myPlayerId }
    
    LaunchedEffect(state.phase) {
        if(state.phase == GamePhase.GAME_OVER) {
            onGameEnded()
        }
    }
    
    val bgGradient = if (state.phase == GamePhase.NIGHT) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface
    
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, com.example.ui.theme.BorderMedium, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sizning Rolingiz", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), letterSpacing = 2.sp, textDecoration = null, fontWeight = FontWeight.Normal, style = androidx.compose.ui.text.TextStyle(textIndent = androidx.compose.ui.text.style.TextIndent.None).copy(lineHeight = 12.sp, fontFeatureSettings = "ss01"))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text((myPlayer?.role?.name ?: "UNKNOWN").uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, letterSpacing = 0.sp)
                    }
                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, com.example.ui.theme.BorderMedium, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                        Text("ℹ️", fontSize = 18.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { if (viewModel.isHost) viewModel.nextPhase() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("🔪 ACTION / NEXT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, com.example.ui.theme.BorderSubtle, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                         Text("🎤", fontSize = 20.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Connected via: Local Wi-Fi", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), letterSpacing = 2.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(bgGradient)
        ) {
            // Header: Room Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, com.example.ui.theme.BorderSubtle)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Local Room: ${state.roomId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("THE FAMILY", fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.primary)) // Pulsing dot mock
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(state.phase.name.replace("_", " "), fontSize = 10.sp, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("00:00", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
            }
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.players) { player ->
                    val isMe = player.id == viewModel.myPlayerId
                    val isDead = !player.isAlive
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = if (isDead) 0.3f else 0.5f))
                            .border(1.dp, if(isMe) MaterialTheme.colorScheme.secondary.copy(alpha=0.3f) else com.example.ui.theme.BorderSubtle, RoundedCornerShape(16.dp))
                            .clickable {
                                if (!isMe && !isDead && state.phase == GamePhase.NIGHT) {
                                    viewModel.submitNightAction(player.id)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isDead) MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(if(isMe) 1.dp else 0.dp, if(isMe) MaterialTheme.colorScheme.secondary.copy(alpha=0.5f) else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(20.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(player.name, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if(isMe) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}
