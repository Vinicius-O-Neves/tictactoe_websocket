package com.tictactoe.models

import kotlinx.serialization.Serializable

@Serializable
data class MakeTurn(
    val player: Player,
    val row: Int,
    val column: Int
)