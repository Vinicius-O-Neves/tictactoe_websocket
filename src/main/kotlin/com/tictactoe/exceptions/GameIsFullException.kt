package com.tictactoe.exceptions

class GameIsFullException(
    override val message: String = "Game is full"
) : Exception()