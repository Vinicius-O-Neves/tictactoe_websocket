package com.tictactoe.utils

import com.tictactoe.enums.ApplicationResultStatus
import com.tictactoe.models.GameState

sealed class ApplicationResult(
    status: ApplicationResultStatus,
    val game: GameState? = null
) {
    class SUCCESS(game: GameState?) : ApplicationResult(status = ApplicationResultStatus.SUCCESS, game = game)

    data object FAILURE : ApplicationResult(status = ApplicationResultStatus.FAILURE)
}