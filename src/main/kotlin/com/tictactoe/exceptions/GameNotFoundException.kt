package com.tictactoe.exceptions

class GameNotFoundException(
    override val message: String = "Game not found"
) : Exception()