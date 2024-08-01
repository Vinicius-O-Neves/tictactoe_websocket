package com.tictactoe.controller.di

import com.tictactoe.controller.game.TicTacToeGameController
import com.tictactoe.controller.game.TicTacToeGameControllerImpl
import com.tictactoe.controller.room.TicTacToeRoomController
import com.tictactoe.controller.room.TicTacToeRoomControllerImpl
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectTicTacToeController() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            ticTacToeControllerModule
        )
    )
}

private val ticTacToeControllerModule = module {
    single<TicTacToeRoomController> { TicTacToeRoomControllerImpl() }
    single<TicTacToeGameController> { TicTacToeGameControllerImpl() }
}