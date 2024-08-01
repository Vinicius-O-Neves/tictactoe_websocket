package com.tictactoe.models

import com.tictactoe.enums.PlayerType
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ConnectedPlayers(
    val gameId: String,
    var player: MutableList<Player> = mutableListOf()
)

@Serializable
data class Player(
    val id: String = UUID.randomUUID().toString(),
    val type: PlayerType
)
