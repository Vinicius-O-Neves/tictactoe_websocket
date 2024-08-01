package com.tictactoe.controller.game

import com.tictactoe.utils.ApplicationResult
import com.tictactoe.enums.PlayerType
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface TicTacToeGameController {

    fun finishTurn(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        row: Int,
        column: Int,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

    fun askToStartNewGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

    fun startNewRound(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

    fun setPlayAgainStatus(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        playAgain: Boolean,
        playerType: PlayerType,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

}