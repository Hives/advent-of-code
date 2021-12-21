package days.day21

import lib.checkAnswer
import lib.time
import java.lang.Integer.min

fun main() {
    val input = Pair(7, 10)
    val testInput = Pair(4, 8)

    time(message = "Part 1", iterations = 1_000, warmUpIterations = 100) {
        part1(input)
    }.checkAnswer(802452)

    time(message = "Part 2", iterations = 1, warmUpIterations = 0) {
        part2(input)
    }.checkAnswer(270005289024391)
}

fun part1(input: Pair<Int, Int>): Int {
    val (player1Start, player2Start) = input

    val player1 = Player(player1Start, 0)
    val player2 = Player(player2Start, 0)

    val die = DeterministicDie(100)

    tailrec fun go(player1: Player, player2: Player, turn: Int): Triple<Player, Player, Int> {
        return if (player1.score >= 1000 || player2.score >= 1000) Triple(player1, player2, turn)
        else {
            val roll = die.take3()
            if (turn % 2 == 0) go(player1, player2.move(roll), turn + 1)
            else go(player1.move(roll), player2, turn + 1)
        }
    }

    val (player1Final, player2Final, turn) = go(player1, player2, 1)

    return min(player1Final.score, player2Final.score) * ((turn - 1) * 3)
}

fun part2(input: Pair<Int, Int>): Long {
    val (player1Start, player2Start) = input

    val initialState = State(
        player1 = Player(
            position = player1Start,
            score = 0
        ),
        player2 = Player(
            position = player2Start,
            score = 0
        ),
        count = 1
    )

    val rolls = listOf(
        Pair(3, 1),
        Pair(4, 3),
        Pair(5, 6),
        Pair(6, 7),
        Pair(7, 6),
        Pair(8, 3),
        Pair(9, 1),
    )

    var player1Wins = 0L
    var player2Wins = 0L

    tailrec fun go(unfinishedStates: List<State>, turn: Int): Unit =
        if (unfinishedStates.isEmpty()) Unit
        else {
//            println("turn: $turn, unfinished states: ${unfinishedStates.size}")

            val newStates = unfinishedStates.flatMap { oldState ->
                rolls.mapNotNull { (roll, count) ->
                    val newCount = oldState.count * count

                    if (turn % 2 == 0) {
                        val newPlayer2 = oldState.player2.move(roll)
                        if (newPlayer2.score >= 21) {
                            player2Wins += newCount
                            null
                        } else {
                            oldState.copy(
                                player2 = newPlayer2,
                                count = newCount
                            )
                        }
                    } else {
                        val newPlayer1 = oldState.player1.move(roll)
                        if (newPlayer1.score >= 21) {
                            player1Wins += newCount
                            null
                        } else {
                            oldState.copy(
                                player1 = newPlayer1,
                                count = newCount
                            )
                        }
                    }
                }
            }

            go(newStates, turn + 1)
        }

    go(listOf(initialState), 1)

    return maxOf(player1Wins, player2Wins)
}

data class DeterministicDie(val max: Int) {
    private var value = max

    fun take3() = next() + next() + next()

    private fun next(): Int {
        val next = if (value == max) 1 else value + 1
        value = next
        return value
    }
}

data class State(val player1: Player, val player2: Player, val count: Long)

data class Player(val position: Int, val score: Int) {
    fun move(roll: Int): Player {
        val newPosition = (((position - 1) + roll) % 10) + 1
        return Player(newPosition, score + newPosition)
    }
}