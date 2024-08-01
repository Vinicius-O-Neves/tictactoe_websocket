package com.tictactoe

import com.tictactoe.controller.game.TicTacToeGameController
import com.tictactoe.controller.room.TicTacToeRoomController
import com.tictactoe.di.TicTacToeModules
import com.tictactoe.plugins.*
import com.tictactoe.usecases.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    startKoin {
        module {
            TicTacToeModules.getModules()
        }
    }

    EngineMain.main(args)
}

fun Application.module() {
    val ticTacToeGameController by inject<TicTacToeGameController>()
    val ticTacToeRoomController by inject<TicTacToeRoomController>()

    val ticTacToeGame = TicTacToeGame(
        ticTacToeInteractors = TicTacToeInteractors(
            ticTacToeConnectPlayerUseCase = TicTacToeConnectPlayerUseCase(
                ticTacToeRoomController = ticTacToeRoomController
            ),
            ticTacToeCreateGameUseCase = TicTacToeCreateGameUseCase(
                ticTacToeRoomController = ticTacToeRoomController
            ),
            ticTacToeDisconnectPlayerUseCase = TicTacToeDisconnectPlayerUseCase(
                ticTacToeRoomController = ticTacToeRoomController
            ),
            ticTacToeFinishTurnUseCase = TicTacToeFinishTurnUseCase(
                ticTacToeGameController = ticTacToeGameController
            ),
            ticTacToeListGamesUseCase = TicTacToeListGamesUseCase(
                ticTacToeRoomController = ticTacToeRoomController
            ),
            ticTacToeStartNewRoundUseCase = TicTacToeStartNewRoundUseCase(
                ticTacToeGameController = ticTacToeGameController
            )
        )
    )

    configureSockets(
        ticTacToeGame = ticTacToeGame,
        sessions = ticTacToeGame.playerSockets
    )
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
