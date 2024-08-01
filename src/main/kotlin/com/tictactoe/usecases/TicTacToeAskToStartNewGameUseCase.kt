package com.tictactoe.usecases

import com.tictactoe.controller.game.TicTacToeGameController
import com.tictactoe.enums.PlayerType
import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeAskToStartNewGameUseCase(
    private val ticTacToeGameController: TicTacToeGameController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = askToStartNewGame(
        ticTacToeGames = ticTacToeGames,
        gameId = gameId,
        sessions = sessions
    )

    private fun askToStartNewGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = ticTacToeGameController.askToStartNewGame(
        ticTacToeGames = ticTacToeGames,
        gameId = gameId,
        sessions = sessions
    )

}