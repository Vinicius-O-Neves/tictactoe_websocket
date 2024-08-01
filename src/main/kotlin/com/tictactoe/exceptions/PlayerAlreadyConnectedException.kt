package com.tictactoe.exceptions

class PlayerAlreadyConnectedException(
    override val message: String = "Você já está conectado a uma sala!"
) : Exception()