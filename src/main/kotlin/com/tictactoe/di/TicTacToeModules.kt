package com.tictactoe.di

import com.tictactoe.TicTacToeGame
import com.tictactoe.controller.di.injectTicTacToeController
import com.tictactoe.usecases.di.injectTicTacToeUseCases
import org.koin.core.context.startKoin
import org.koin.dsl.module

object TicTacToeModules {
    fun getModules() = listOf(
        injectTicTacToeController(),
        injectTicTacToeUseCases(),
        injectTicTacToeGame()
    )
}

private fun injectTicTacToeGame() =
    module {
        factory {
            TicTacToeGame(get())
        }
    }