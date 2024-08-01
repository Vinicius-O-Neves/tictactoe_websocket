package com.tictactoe.controller.room

import com.google.gson.Gson
import com.tictactoe.controller.utils.GameControllerComponent
import com.tictactoe.controller.utils.GameControllerDelegate
import com.tictactoe.enums.ApplicationResultStatus
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.enums.PlayerType
import com.tictactoe.exceptions.*
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import com.tictactoe.utils.gameControllerHandler
import com.tictactoe.utils.sendException
import com.tictactoe.utils.sendMessage
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class TicTacToeRoomControllerImpl : TicTacToeRoomController, GameControllerComponent by GameControllerDelegate() {

    override fun listGames(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession
    ): Flow<ApplicationResultStatus> = flow {
        try {
            val games = ticTacToeGames.value

            val dataInJson = Gson().toJson(games)

            gameScope.launch {
                session.send(dataInJson)
            }

            emit(ApplicationResultStatus.SUCCESS)
        } catch (e: Exception) {
            emit(ApplicationResultStatus.FAILURE)
        }
    }

    override fun createGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = gameControllerHandler(
        block = {
            val newGame = GameState()

            with(newGame.connectedPlayers) {
                val newPlayer = Player(type = newGame.currentPlayerType)

                this.player.add(newPlayer)

                sessions.update { currentSessions ->
                    currentSessions.apply {
                        this[newPlayer.id] = session
                    }
                }
            }

            ticTacToeGames.update { games ->
                games + newGame
            }

            gameScope.launch {
                broadcast(gameState = newGame, sessions = sessions)
            }

            newGame
        },
        exception = CreateGameException()
    )

    override fun connectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        session: WebSocketSession,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = gameControllerHandler(
        block = {
            val game = gameId.findGame(
                ticTacToeGames = ticTacToeGames
            )

            if (game == null) {
                gameScope.launch {
                    session.sendException(
                        exceptionMessage = GameNotFoundException().message
                    )

                    session.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Game not found"))
                }
            }

            return@gameControllerHandler game?.let {
                val hasPlayerX = game.connectedPlayers.player.any { player ->
                    player.type == PlayerType.X
                }
                val playerType = if (hasPlayerX) PlayerType.O else PlayerType.X

                with(game.connectedPlayers) {
                    val newPlayer = Player(type = playerType)

                    if (player.size == 2) {
                        gameScope.launch {
                            session.sendException(
                                exceptionMessage = GameIsFullException().message
                            )
                        }
                    }

                    if (player.any { existingPlayer -> existingPlayer.id == newPlayer.id }) {
                        gameScope.launch {
                            session.sendException(
                                exceptionMessage = PlayerAlreadyConnectedException().message
                            )

                            session.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Player already connected"))
                        }

                        return@let game
                    }

                    if (sessions.value.contains(newPlayer.id).not()) {
                        sessions.update { currentSessions ->
                            currentSessions.apply {
                                this[newPlayer.id] = session
                            }
                        }
                    }

                    game.connectedPlayers.player.add(newPlayer)

                    updateGameState(
                        ticTacToeGames = ticTacToeGames,
                        gameId = gameId,
                        board = game.board,
                        currentPlayerType = game.currentPlayerType,
                        winner = game.winner,
                        sessions = sessions
                    )
                }

                game
            }
        },
        exception = PlayerAlreadyConnectedException()
    )

    override fun disconnectPlayer(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = gameControllerHandler(
        block = {
            val game = gameId.findGame(
                ticTacToeGames = ticTacToeGames
            )

            if (game == null) {
                gameScope.launch {
                    val session = sessions.value[player.id]

                    session?.send(GameNotFoundException().message ?: "game_not_found")
                }
            }

            return@gameControllerHandler game?.let {
                game.connectedPlayers.player.remove(player)

                updateGameState(
                    ticTacToeGames = ticTacToeGames,
                    gameId = gameId,
                    board = game.board,
                    currentPlayerType = game.currentPlayerType,
                    winner = game.winner,
                    sessions = sessions
                )

                sessions.update { currentSessions ->
                    currentSessions.apply {
                        remove(player.id)
                    }
                }

                game
            }
        },
        exception = DisconnectPlayerException()
    )

    private fun updateGameState(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        board: Array<Array<PlayerType?>>,
        currentPlayerType: PlayerType,
        winner: PlayerType? = null,
        askToStartNewGame: Boolean = false,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) {
        ticTacToeGames.update { games ->
            games.map { game ->
                if (game.id == gameId) {
                    val updatedGame = game.copy(
                        id = gameId,
                        board = board,
                        currentPlayerType = currentPlayerType,
                        winner = winner,
                        askToStartNewGame = askToStartNewGame
                    )

                    gameScope.launch {
                        broadcast(gameState = updatedGame, sessions = sessions)
                    }

                    updatedGame
                } else {
                    game
                }
            }
        }
    }

}