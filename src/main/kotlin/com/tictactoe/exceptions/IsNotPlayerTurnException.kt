package com.tictactoe.exceptions

class IsNotPlayerTurnException(
   override val message: String = "Não é a sua vez de jogar. Aguarde!"
): Exception()