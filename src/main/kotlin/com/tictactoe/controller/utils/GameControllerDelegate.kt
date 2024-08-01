package com.tictactoe.controller.utils

import com.google.gson.Gson
import com.tictactoe.models.GameState
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class GameControllerDelegate : GameControllerComponent {

    override val gameScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override var delayGameJob: Job? = null

    val gson = Gson()

    override fun String.findGame(
        ticTacToeGames: MutableStateFlow<List<GameState>>
    ): GameState? {
        val game = ticTacToeGames.value.find {
            it.id == this
        }

        return game
    }

    override fun broadcast(
        gameState: GameState,
        sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
    ): GameState {
        gameScope.launch {
            val gameJson = gson.toJson(gameState)

            val playersId = gameState.connectedPlayers.player.map { player -> player.id }

            sessions.value.forEach { (player, socket) ->
                if (player in playersId) {
                    socket.send(Frame.Text("game: $gameJson"))
                }
            }
        }

        return gameState
    }

}