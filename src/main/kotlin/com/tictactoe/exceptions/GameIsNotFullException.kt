package com.tictactoe.exceptions

class GameIsNotFullException(
    override val message: String = "O jogo precisa de 2 jogadores para começar"
) : Exception()