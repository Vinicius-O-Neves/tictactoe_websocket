package com.tictactoe.routes

import com.tictactoe.TicTacToeGame
import com.tictactoe.enums.ApplicationResultStatus
import com.tictactoe.models.Player
import com.tictactoe.utils.ApplicationResult
import com.tictactoe.utils.extractAction
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow

fun Route.socket(
    ticTacToeGame: TicTacToeGame,
    sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
) {
    webSocket(
        path = "/tictactoe/games"
    ) {
        try {
            ticTacToeGame.ticTacToeInteractors.ticTacToeListGamesUseCase(
                ticTacToeGames = ticTacToeGame.ticTacToeGames,
                session = this
            ).collect { applicationResultStatus ->
                if (applicationResultStatus == ApplicationResultStatus.FAILURE) {
                    close(
                        reason = CloseReason(
                            code = CloseReason.Codes.INTERNAL_ERROR,
                            message = "Failed list games"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

            outgoing.send(
                Frame.Text(
                    e.message ?: "Failed list games"
                )
            )
        }
    }

    webSocket(
        path = "/tictactoe/create_game"
    ) {
        var gameId: String? = null
        var player: Player? = null

        try {
            ticTacToeGame.ticTacToeInteractors.ticTacToeCreateGameUseCase(
                ticTacToeGames = ticTacToeGame.ticTacToeGames,
                session = this,
                sessions = sessions
            ).collect { applicationResult ->
                if (applicationResult == ApplicationResult.FAILURE) {
                    close(
                        reason = CloseReason(
                            code = CloseReason.Codes.INTERNAL_ERROR,
                            message = "Failed to create game"
                        )
                    )
                }

                gameId = applicationResult.game?.id
                player = applicationResult.game?.connectedPlayers?.player?.first()

                gameId?.let { gameId ->
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val action = extractAction(
                                message = frame.readText()
                            )

                            ticTacToeGame.ticTacToeInteractors.ticTacToeFinishTurnUseCase(
                                ticTacToeGames = ticTacToeGame.ticTacToeGames,
                                player = action.player,
                                gameId = gameId,
                                row = action.row,
                                column = action.column,
                                sessions = sessions
                            ).collect { applicationResult ->
                                if (applicationResult == ApplicationResult.FAILURE) {
                                    close(
                                        reason = CloseReason(
                                            code = CloseReason.Codes.INTERNAL_ERROR,
                                            message = "Failed to make move"
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

            outgoing.send(
                Frame.Text(
                    e.message ?: "Failed to create game"
                )
            )
        } finally {
            player?.let { actualPlayer ->
                ticTacToeGame.ticTacToeInteractors.ticTacToeDisconnectPlayerUseCase(
                    ticTacToeGames = ticTacToeGame.ticTacToeGames,
                    player = actualPlayer,
                    gameId = gameId ?: "",
                    sessions = sessions
                ).collect {
                    ticTacToeGame.ticTacToeInteractors.ticTacToeStartNewRoundUseCase(
                        ticTacToeGames = ticTacToeGame.ticTacToeGames,
                        gameId = gameId ?: "",
                        sessions = sessions
                    ).collect {}
                }
            }
        }
    }

    webSocket(
        path = "tictactoe/connect_player/{gameId}"
    ) {
        val gameId = call.parameters["gameId"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "Missing gameId"
            )
        )
        var player: Player? = null

        try {
            ticTacToeGame.ticTacToeInteractors.ticTacToeConnectPlayerUseCase(
                ticTacToeGames = ticTacToeGame.ticTacToeGames,
                gameId = gameId,
                session = this,
                sessions = sessions
            ).collect { applicationResult ->
                if (applicationResult == ApplicationResult.FAILURE) {
                    close(
                        reason = CloseReason(
                            code = CloseReason.Codes.INTERNAL_ERROR,
                            message = "Failed to connect player"
                        )
                    )
                }

                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val action = extractAction(
                            message = frame.readText()
                        )

                        ticTacToeGame.ticTacToeInteractors.ticTacToeFinishTurnUseCase(
                            ticTacToeGames = ticTacToeGame.ticTacToeGames,
                            player = action.player,
                            gameId = gameId,
                            row = action.row,
                            column = action.column,
                            sessions = sessions
                        ).collect { applicationResult ->
                            player = applicationResult.game?.connectedPlayers?.player?.find { player ->
                                player.id == action.player.id
                            }

                            if (applicationResult == ApplicationResult.FAILURE) {
                                close(
                                    reason = CloseReason(
                                        code = CloseReason.Codes.INTERNAL_ERROR,
                                        message = "Failed to make move"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            player?.let { actualPlayer ->
                ticTacToeGame.ticTacToeInteractors.ticTacToeDisconnectPlayerUseCase(
                    ticTacToeGames = ticTacToeGame.ticTacToeGames,
                    player = actualPlayer,
                    gameId = gameId ?: "",
                    sessions = sessions
                ).collect {
                    ticTacToeGame.ticTacToeInteractors.ticTacToeStartNewRoundUseCase(
                        ticTacToeGames = ticTacToeGame.ticTacToeGames,
                        gameId = gameId ?: "",
                        sessions = sessions
                    ).collect {}
                }
            }
        }
    }

}