package days.day02

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day02.txt").strings()
    val exampleInput = Reader("day02-example.txt").strings()

    time(message = "Part 1") {
        Part1(input).go()
    }.checkAnswer(8933)

    time(message = "Part 2") {
        Part2(input).go()
    }.checkAnswer(11998)
}

class Part1(val input: List<String>) {
    fun go(): Int {
        return input.fold(0) { acc, round ->
            val (p1, p2) = parse(round)
            acc + p2.score + p2Score(p1, p2)
        }
    }

    private fun parse(round: String): List<Move> = round.split(" ").map(Move::from)

    private enum class Move(val score: Int) {
        ROCK(1), PAPER(2), SCISSORS(3);

        companion object {
            fun from(input: String) =
                when {
                    input == "A" || input == "X" -> ROCK
                    input == "B" || input == "Y" -> PAPER
                    else -> SCISSORS
                }
        }
    }

    private fun p2Score(p1: Move, p2: Move): Int =
        when {
            p1 == p2 -> 3
            p2 == Move.ROCK && p1 == Move.SCISSORS -> 6
            p2 == Move.PAPER && p1 == Move.ROCK -> 6
            p2 == Move.SCISSORS && p1 == Move.PAPER -> 6
            else -> 0
        }

}

class Part2(val input: List<String>) {
    fun go(): Int {
        return input.fold(0) { acc, round ->
            val (p1, p2) = parse(round)
            acc + p2.score + p2Score(p1, p2)
        }
    }

    private fun parse(round: String): List<Move> = round.split(" ").let { (first, second) ->
        val p1 = Move.from(first)
        val p2 = when {
            second == "Y" -> p1
            second == "X" -> when {
                p1 == Move.ROCK -> Move.SCISSORS
                p1 == Move.PAPER -> Move.ROCK
                else -> Move.PAPER
            }

            else -> when {
                p1 == Move.ROCK -> Move.PAPER
                p1 == Move.PAPER -> Move.SCISSORS
                else -> Move.ROCK
            }
        }
        listOf(p1, p2)
    }

    private enum class Move(val score: Int) {
        ROCK(1), PAPER(2), SCISSORS(3);

        companion object {
            fun from(input: String) =
                when {
                    input == "A" || input == "X" -> ROCK
                    input == "B" || input == "Y" -> PAPER
                    else -> SCISSORS
                }
        }
    }

    private fun p2Score(p1: Move, p2: Move): Int =
        when {
            p1 == p2 -> 3
            p2 == Move.ROCK && p1 == Move.SCISSORS -> 6
            p2 == Move.PAPER && p1 == Move.ROCK -> 6
            p2 == Move.SCISSORS && p1 == Move.PAPER -> 6
            else -> 0
        }

}
