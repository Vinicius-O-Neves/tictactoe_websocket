package com.tictactoe.controller.game

import com.tictactoe.controller.utils.GameControllerComponent
import com.tictactoe.controller.utils.GameControllerDelegate
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.enums.PlayerType
import com.tictactoe.exceptions.*
import com.tictactoe.models.GameState
import com.tictactoe.models.Player
import com.tictactoe.models.PlayerWantRematch
import com.tictactoe.utils.gameControllerHandler
import com.tictactoe.utils.sendException
import com.tictactoe.utils.sendMessage
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TicTacToeGameControllerImpl : TicTacToeGameController, GameControllerComponent by GameControllerDelegate() {

    override fun finishTurn(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        player: Player,
        gameId: String,
        row: Int,
        column: Int,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = gameControllerHandler(
        block = {
            val game = gameId.findGame(
                ticTacToeGames = ticTacToeGames
            )

            val playerSession = sessions.value[player.id]

            if (game == null) {
                gameScope.launch {
                    playerSession?.sendException(
                        exceptionMessage = GameNotFoundException().message
                    )
                }
            }

            return@gameControllerHandler game?.let {
                val error = getError(game, player, row, column)

                if (error != null) {
                    gameScope.launch {
                        when (error) {
                            is GameIsNotFullException -> {
                                playerSession?.sendException(
                                    exceptionMessage = error.message
                                )
                            }

                            is IsNotPlayerTurnException -> {
                                playerSession?.sendException(
                                    exceptionMessage = error.message
                                )
                            }

                            is InvalidMoveException -> {
                                playerSession?.sendException(
                                    exceptionMessage = error.message
                                )
                            }

                            is UserIsNotPlayingException -> {
                                playerSession?.sendException(
                                    exceptionMessage = error.message
                                )
                            }
                        }
                    }

                    return@gameControllerHandler game
                }

                val newBoard = game.board.also { field ->
                    field[row][column] = game.currentPlayerType
                }

                if (game.isBoardFull()) {
                    askToStartNewGame(
                        ticTacToeGames = ticTacToeGames,
                        gameId = gameId,
                        sessions = sessions
                    )
                }

                updateGameState(
                    ticTacToeGames = ticTacToeGames,
                    gameId = gameId,
                    board = newBoard,
                    currentPlayerType = if (game.currentPlayerType == PlayerType.X) PlayerType.O else PlayerType.X,
                    winner = game.determineWinner().also { winner ->
                        winner?.let {
                            sessions.value.forEach { session ->
                                gameScope.launch {
                                    session.value.sendMessage(
                                        message = "O jogador ${winner.name} venceu!"
                                    )
                                }
                            }

                            gameScope.launch {
                                startNewRound(
                                    ticTacToeGames = ticTacToeGames,
                                    gameId = gameId,
                                    sessions = sessions
                                ).collect {}
                            }
                        }
                    },
                    players = game.connectedPlayers.player,
                    sessions = sessions
                )

                game
            }
        },
        exception = FinishTurnException()
    )

    override fun askToStartNewGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> =
        gameControllerHandler(
            block = {
                val game = gameId.findGame(
                    ticTacToeGames = ticTacToeGames
                )

                return@gameControllerHandler game?.let {
                    if (game.connectedPlayers.player.size != 2) {
                        throw GameIsNotFullException()
                    }

                    updateGameState(
                        ticTacToeGames = ticTacToeGames,
                        gameId = gameId,
                        board = game.board,
                        currentPlayerType = game.currentPlayerType,
                        winner = null,
                        players = game.connectedPlayers.player,
                        askToStartNewGame = true,
                        sessions = sessions
                    )

                    game
                }
            },
            exception = GameIsNotFullException()
        )

    override fun startNewRound(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): Flow<ApplicationResult> = gameControllerHandler(
        block = {
            gameScope.launch {
                ticTacToeGames.update { games ->
                    games.map { game ->
                        if (game.id == gameId) {
                            val updatedGame = game.copy(
                                board = GameState.emptyBoard(),
                                currentPlayerType = GameState.getFirstPlayer(),
                                winner = null
                            )

                            updateGameState(
                                ticTacToeGames = ticTacToeGames,
                                gameId = gameId,
                                board = updatedGame.board,
                                currentPlayerType = updatedGame.currentPlayerType,
                                winner = updatedGame.winner,
                                players = updatedGame.connectedPlayers.player,
                                sessions = sessions
                            )

                            updatedGame
                        } else {
                            game
                        }
                    }
                }
            }

            val game = gameId.findGame(
                ticTacToeGames = ticTacToeGames
            )

            game
        },
        exception = GameIsNotFullException()
    )

    override fun setPlayAgainStatus(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        playAgain: Boolean,
        playerType: PlayerType,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) = gameControllerHandler(
        block = {
            val game = gameId.findGame(
                ticTacToeGames = ticTacToeGames
            )

            return@gameControllerHandler game?.let {
                if (playerType == PlayerType.X) {
                    game.shouldStartNewGame.copy(
                        first = mapOf(playerType to playAgain),
                    )
                } else {
                    game.shouldStartNewGame.copy(
                        second = mapOf(playerType to playAgain),
                    )
                }

                updateGameState(
                    ticTacToeGames = ticTacToeGames,
                    gameId = gameId,
                    board = game.board,
                    currentPlayerType = game.currentPlayerType,
                    winner = null,
                    players = game.connectedPlayers.player,
                    askToStartNewGame = false,
                    shouldStartNewGame = game.shouldStartNewGame,
                    sessions = sessions
                )

                if (game.shouldStartNewGame.first.values.first() && game.shouldStartNewGame.second.values.first()) {
                    startNewRound(ticTacToeGames, gameId, sessions = sessions)
                }

                game
            }
        }, exception = GameIsNotFullException()
    )

    private fun updateGameState(
        ticTacToeGames: MutableStateFlow<List<GameState>>,
        gameId: String,
        board: Array<Array<PlayerType?>>,
        currentPlayerType: PlayerType,
        winner: PlayerType? = null,
        players: MutableList<Player>,
        askToStartNewGame: Boolean = false,
        shouldStartNewGame: Pair<PlayerWantRematch, PlayerWantRematch> = Pair(mapOf(), mapOf()),
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ) {
        ticTacToeGames.update { games ->
            games.map { game ->
                if (game.id == gameId) {
                    val currentPlayer = game.winner ?: currentPlayerType

                    val updatedGame = game.copy(
                        id = gameId,
                        board = board,
                        currentPlayerType = currentPlayer,
                        winner = winner,
                        connectedPlayers = game.connectedPlayers.copy(
                            player = players
                        ),
                        askToStartNewGame = askToStartNewGame,
                        shouldStartNewGame = shouldStartNewGame
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

    private fun getError(game: GameState, player: Player? = null, row: Int, column: Int): Exception? {
        return when {
            player?.id !in game.connectedPlayers.player.map { it.id } -> UserIsNotPlayingException()
            game.currentPlayerType != player?.type -> IsNotPlayerTurnException()
            game.connectedPlayers.player.size < 2 -> GameIsNotFullException()
            game.board[row][column] != null -> InvalidMoveException()
            else -> null
        }
    }

}