package com.tictactoe.exceptions

class DisconnectPlayerException(): Exception() {
    override val message: String = "Error disconnecting player"
}