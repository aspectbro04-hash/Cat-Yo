package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onNavigateToLobby: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MAFIA",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 8.sp
        )
        Text(
            text = "LOCAL",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 4.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                viewModel.hostGame(if(playerName.isBlank()) "Host" else playerName)
                onNavigateToLobby()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Create Room", fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = {
                viewModel.joinGame(if(playerName.isBlank()) "Player" else playerName)
                onNavigateToLobby()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Join Room Nearby", fontSize = 18.sp)
        }
    }
}
