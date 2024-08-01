package com.tictactoe.usecases

import com.tictactoe.controller.game.TicTacToeGameController
import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeStartNewRoundUseCase(
    private val ticTacToeGameController: TicTacToeGameController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = startNewRound(
        ticTacToeGames = ticTacToeGames,
        gameId = gameId,
        sessions = sessions
    )

    private fun startNewRound(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = ticTacToeGameController.startNewRound(
        ticTacToeGames = ticTacToeGames,
        gameId = gameId,
        sessions = sessions
    )

}