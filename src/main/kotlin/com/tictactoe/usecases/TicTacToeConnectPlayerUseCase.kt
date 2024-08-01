package com.tictactoe.usecases

import com.tictactoe.controller.room.TicTacToeRoomController
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeConnectPlayerUseCase(
    private val ticTacToeRoomController: TicTacToeRoomController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = connectPlayer(
        ticTacToeGames = ticTacToeGames, session = session, gameId = gameId, sessions = sessions
    )

    private fun connectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = ticTacToeRoomController.connectPlayer(
        ticTacToeGames = ticTacToeGames, session = session, gameId = gameId, sessions = sessions
    )

}