package com.tictactoe.usecases.di

import com.tictactoe.usecases.*
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectTicTacToeUseCases() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            ticTacToeUseCases
        )
    )
}

private val ticTacToeUseCases = module {
    factory { TicTacToeListGamesUseCase(get()) }
    factory { TicTacToeAskToStartNewGameUseCase(get()) }
    factory { TicTacToeConnectPlayerUseCase(get()) }
    factory { TicTacToeCreateGameUseCase(get()) }
    factory { TicTacToeDisconnectPlayerUseCase(get()) }
    factory { TicTacToeFinishTurnUseCase(get()) }
    factory { TicTacToeStartNewRoundUseCase(get()) }
    factory { TicTacToeSetPlayAgainStatusUseCase(get()) }
    factory { TicTacToeInteractors(get(), get(), get(), get(), get(), get()) }
}