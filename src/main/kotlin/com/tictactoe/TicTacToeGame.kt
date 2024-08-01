package com.tictactoe

import com.tictactoe.models.GameState
import com.tictactoe.usecases.TicTacToeInteractors
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeGame(
    val ticTacToeInteractors: TicTacToeInteractors
) {

    val ticTacToeGames = MutableStateFlow(emptyList<GameState>())

    val playerSockets = MutableStateFlow((mutableMapOf<String, WebSocketSession>()))

}