package com.tictactoe.controller.room

import com.tictactoe.enums.ApplicationResultStatus
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.enums.PlayerType
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface TicTacToeRoomController {

    fun listGames(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession
    ): Flow<ApplicationResultStatus>

    fun createGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

    fun connectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

    fun disconnectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult>

}