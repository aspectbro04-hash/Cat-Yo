package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.game.GameViewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MafiaApp()
      }
    }
  }
}

@Composable
fun MafiaApp() {
  val context = androidx.compose.ui.platform.LocalContext.current
  val application = context.applicationContext as android.app.Application
  val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
  
  val navController = rememberNavController()
  val viewModel: GameViewModel = viewModel(factory = factory)
  
  NavHost(navController = navController, startDestination = "main_menu") {
    composable("main_menu") {
      MainMenuScreen(
        viewModel = viewModel,
        onNavigateToLobby = { navController.navigate("lobby") }
      )
    }
    composable("lobby") {
      LobbyScreen(
        viewModel = viewModel,
        onGameStarted = {
          navController.navigate("game") {
            popUpTo("lobby") { inclusive = true }
          }
        }
      )
    }
    composable("game") {
      GameScreen(
        viewModel = viewModel,
        onGameEnded = {
          navController.navigate("main_menu") {
            popUpTo("game") { inclusive = true }
          }
        }
      )
    }
  }
}
