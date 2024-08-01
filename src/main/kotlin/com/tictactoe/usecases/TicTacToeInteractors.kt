package com.tictactoe.usecases

class TicTacToeInteractors(
    val ticTacToeListGamesUseCase: TicTacToeListGamesUseCase,
    val ticTacToeConnectPlayerUseCase: TicTacToeConnectPlayerUseCase,
    val ticTacToeCreateGameUseCase: TicTacToeCreateGameUseCase,
    val ticTacToeDisconnectPlayerUseCase: TicTacToeDisconnectPlayerUseCase,
    val ticTacToeFinishTurnUseCase: TicTacToeFinishTurnUseCase,
    val ticTacToeStartNewRoundUseCase: TicTacToeStartNewRoundUseCase
)