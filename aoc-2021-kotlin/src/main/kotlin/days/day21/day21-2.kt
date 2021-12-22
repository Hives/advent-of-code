package days.day21

import dev.forkhandles.tuples.Tuple4
import lib.checkAnswer
import lib.time

fun main() {
    val input = Pair(7, 10)
    val testInput = Pair(4, 8)

    time(message = "Part 2", iterations = 3, warmUpIterations = 0) {
        part2_2(input)
    }.checkAnswer(270005289024391)
}

fun part2_2(input: Pair<Int, Int>): Pair<Long, Long> {
    val pos1Init = input.first - 1
    val pos2Init = input.second - 1

    val memo = mutableMapOf<Tuple4<Int, Int, Int, Int>, Pair<Long, Long>>()

    fun countWins(gameState: Tuple4<Int, Int, Int, Int>): Pair<Long, Long> {
        memo[gameState]?.also { return it }

        val (pos1, score1, pos2, score2) = gameState
        if (score1 >= 21) return Pair(1, 0)
        if (score2 >= 21) return Pair(0, 1)

        var p1TotalWins = 0L
        var p2TotalWins = 0L

        listOf(1, 2, 3).forEach { d1 ->
            listOf(1, 2, 3).forEach { d2 ->
                listOf(1, 2, 3).forEach { d3 ->
                    val newPos1 = (pos1 + d1 + d2 + d3) % 10
                    val newScore1 = score1 + newPos1 - 1
                    val (p2Wins, p1Wins) = countWins(Tuple4(pos2, score2, newPos1, newScore1))
                    p1TotalWins += p1Wins
                    p2TotalWins += p2Wins
                }
            }
        }

        memo[gameState] = Pair(p1TotalWins, p2TotalWins)

        return Pair(p1TotalWins, p2TotalWins)
    }

    return countWins(Tuple4(pos1Init, 0, pos2Init, 0))
}

