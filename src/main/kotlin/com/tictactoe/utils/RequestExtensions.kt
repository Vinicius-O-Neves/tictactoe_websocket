package com.tictactoe.utils

import com.tictactoe.enums.PlayerType
import com.tictactoe.models.GameState
import com.tictactoe.models.MakeTurn
import com.tictactoe.models.Player
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

fun gameControllerHandler(
    block: () -> GameState?,
    exception: Exception
): Flow<ApplicationResult> = flow {
    try {
        val result = block()

        emit(
            ApplicationResult.SUCCESS(
                result
            )
        )
    } catch (e: Exception) {
        emit(ApplicationResult.FAILURE)

        e.printStackTrace()
    }
}

fun extractAction(message: String): MakeTurn {
    val type = message.substringBefore("#")
    val body = message.substringAfter("#")

    return if (type == "make_turn") {
        Json.decodeFromString(body)
    } else MakeTurn(
        Player(
            id = "",
            type = PlayerType.NONE
        ), row = -1, column = -1
    )
}

suspend fun WebSocketSession.sendException(exceptionMessage: String) {
    val dataInJson = Json.encodeToJsonElement(
        "exception: $exceptionMessage"
    )

    this.send(Frame.Text(dataInJson.toString()))
}

suspend fun WebSocketSession.sendMessage(message: String) {
    val dataInJson = Json.encodeToJsonElement(
        "message: $message"
    )

    this.send(Frame.Text(dataInJson.toString()))
}