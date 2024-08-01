package com.tictactoe.exceptions

class UserIsNotPlayingException(
    override val message: String = "Você não está jogando esse jogo."
) : Exception()