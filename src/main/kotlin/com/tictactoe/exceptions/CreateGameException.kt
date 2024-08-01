package com.tictactoe.exceptions

class CreateGameException: Exception() {
    override val message: String = "Failed to create game"
}