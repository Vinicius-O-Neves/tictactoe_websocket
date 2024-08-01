package com.tictactoe.exceptions

class InvalidMoveException(
    override val message: String = "Já houve uma jogada nessa casa"
): Exception()