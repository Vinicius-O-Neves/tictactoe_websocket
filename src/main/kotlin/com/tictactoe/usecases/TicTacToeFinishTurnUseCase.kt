package com.tictactoe.usecases

import com.tictactoe.controller.game.TicTacToeGameController
import com.tictactoe.enums.PlayerType
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeFinishTurnUseCase(
    private val ticTacToeGameController: TicTacToeGameController
) {
    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        row: Int,
        column: Int,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = finishTurn(
        ticTacToeGames = ticTacToeGames,
        player = player,
        gameId = gameId,
        row = row,
        column = column,
        sessions = sessions
    )

    private fun finishTurn(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        row: Int,
        column: Int,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = ticTacToeGameController.finishTurn(
        ticTacToeGames = ticTacToeGames,
        player = player,
        gameId = gameId,
        row = row,
        column = column,
        sessions = sessions
    )

}