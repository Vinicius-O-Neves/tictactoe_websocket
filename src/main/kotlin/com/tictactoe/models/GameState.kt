package com.tictactoe.models

import com.tictactoe.enums.PlayerType
import kotlinx.serialization.Serializable
import java.util.*

typealias PlayerWantRematch = Map<PlayerType, Boolean>

@Serializable
data class GameState(
    val id: String = UUID.randomUUID().toString(),
    val board: Array<Array<PlayerType?>> = emptyBoard(),
    val currentPlayerType: PlayerType = getFirstPlayer(),
    val winner: PlayerType? = null,
    val connectedPlayers: ConnectedPlayers = ConnectedPlayers(gameId = id),
    val askToStartNewGame: Boolean = false,
    val shouldStartNewGame: Pair<PlayerWantRematch, PlayerWantRematch> = Pair(mapOf(), mapOf())
) {
    companion object {
        fun emptyBoard(): Array<Array<PlayerType?>> {
            return Array(3) { Array(3) { null } }
        }

        fun getFirstPlayer(): PlayerType {
            val playerTypes = setOf(PlayerType.X, PlayerType.O)

            return playerTypes.random()
        }
    }

    fun isBoardFull(): Boolean {
        return board.all { row ->
            row.all { columns ->
                columns != null
            }
        }
    }

    fun determineWinner(): PlayerType? {
        val winningCombinations = listOf(
            listOf(
                board[0][0], board[0][1], board[0][2]
            ),
            listOf(
                board[1][0], board[1][1], board[1][2]
            ),
            listOf(
                board[2][0], board[2][1], board[2][2]
            ),
            listOf(
                board[0][0], board[1][0], board[2][0]
            ),
            listOf(
                board[0][1], board[1][1], board[2][1]
            ),
            listOf(
                board[0][2], board[1][2], board[2][2]
            ),
            listOf(
                board[0][0], board[1][1], board[2][2]
            ),
            listOf(
                board[0][2], board[1][1], board[2][0]
            )
        )

        return winningCombinations.map { combination ->
            if (combination.all { it == PlayerType.X }) PlayerType.X
            else if (combination.all { it == PlayerType.O }) PlayerType.O
            else null
        }.first()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (!board.contentDeepEquals(other.board)) return false
        if (currentPlayerType != other.currentPlayerType) return false
        if (winner != other.winner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + (currentPlayerType.hashCode())
        result = 31 * result + (winner?.hashCode() ?: 0)
        return result
    }
}
