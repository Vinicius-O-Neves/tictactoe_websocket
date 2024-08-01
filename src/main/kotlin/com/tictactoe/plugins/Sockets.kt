package com.tictactoe.plugins

import com.tictactoe.TicTacToeGame
import com.tictactoe.routes.socket
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration

fun Application.configureSockets(
    ticTacToeGame: TicTacToeGame,
    sessions: MutableStateFlow<MutableMap<String, WebSocketSession>>
) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        socket(
            ticTacToeGame = ticTacToeGame,
            sessions = sessions
        )
    }
}
