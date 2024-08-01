package com.tictactoe.usecases

import com.tictactoe.controller.room.TicTacToeRoomController
import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeListGamesUseCase(
    private val ticTacToeRoomController: TicTacToeRoomController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession
    ) = listGames(
        ticTacToeGames = ticTacToeGames,
        session = session
    )

    private fun listGames(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession
    ) = ticTacToeRoomController.listGames(
        ticTacToeGames = ticTacToeGames,
        session = session
    )

}