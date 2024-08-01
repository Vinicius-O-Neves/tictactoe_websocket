package com.tictactoe.usecases

import com.tictactoe.controller.room.TicTacToeRoomController
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeCreateGameUseCase(
    private val ticTacToeRoomController: TicTacToeRoomController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession, sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = connectPlayer(
        ticTacToeGames = ticTacToeGames, session = session, sessions = sessions
    )

    private fun connectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = ticTacToeRoomController.createGame(
        ticTacToeGames = ticTacToeGames, session = session, sessions = sessions
    )

}