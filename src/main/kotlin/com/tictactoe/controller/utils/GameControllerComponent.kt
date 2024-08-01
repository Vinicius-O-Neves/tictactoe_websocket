package com.tictactoe.controller.utils

import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

interface GameControllerComponent {

    val gameScope: CoroutineScope
    var delayGameJob: Job?

    fun String.findGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>
    ): GameState?

    fun broadcast(
        gameState: GameState,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): GameState

}