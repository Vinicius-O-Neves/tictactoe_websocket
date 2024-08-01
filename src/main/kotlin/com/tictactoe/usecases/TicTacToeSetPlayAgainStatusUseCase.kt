package com.tictactoe.usecases

import com.tictactoe.controller.game.TicTacToeGameController
import com.tictactoe.enums.PlayerType
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeSetPlayAgainStatusUseCase(
    private val ticTacToeGameController: TicTacToeGameController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        playAgain: Boolean,
        playerType: PlayerType,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = setPlayAgainStatus(
        ticTacToeGames = ticTacToeGames,
        gameId = gameId,
        playAgain = playAgain,
        playerType = playerType,
        sessions = sessions
    )

    private fun setPlayAgainStatus(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        playAgain: Boolean,
        playerType: PlayerType,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = ticTacToeGameController.setPlayAgainStatus(
        ticTacToeGames = ticTacToeGames,
        gameId = gameId,
        playAgain = playAgain,
        playerType = playerType,
        sessions = sessions
    )

}