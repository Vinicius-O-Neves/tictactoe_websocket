package com.tictactoe.usecases

import com.tictactoe.controller.room.TicTacToeRoomController
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TicTacToeDisconnectPlayerUseCase(
    private val ticTacToeRoomController: TicTacToeRoomController
) {

    operator fun invoke(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = disconnectPlayer(
        ticTacToeGames = ticTacToeGames,
        player = player,
        gameId = gameId,
        sessions = sessions
    )

    private fun disconnectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = ticTacToeRoomController.disconnectPlayer(
        ticTacToeGames = ticTacToeGames,
        player = player,
        gameId = gameId,
        sessions = sessions
    )

}