package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "THE FAMILY",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.primary))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOCAL SYNDICATE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.primary))
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        // Input Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "ENTER ALIAS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = BorderMedium,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                placeholder = { Text("e.g. Capone", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    viewModel.hostGame(if(playerName.isBlank()) "Host" else playerName)
                    onNavigateToLobby()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("HOST OPERATION", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    viewModel.joinGame(if(playerName.isBlank()) "Player" else playerName)
                    onNavigateToLobby()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderMedium)
            ) {
                Text("JOIN SYNDICATE", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

