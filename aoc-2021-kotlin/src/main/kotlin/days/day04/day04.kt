package days.day04

import lib.Reader
import lib.checkAnswer
import lib.flip
import lib.time

fun main() {
    val exampleInput = Reader("day04-example.txt").string()
    val input = Reader("day04.txt").string()

    time(iterations = 1_000, warmUpIterations = 500, message = "Part 1") {
        part1(input)
    }.checkAnswer(72770)

    time(iterations = 500, warmUpIterations = 100, message = "Part 2") {
        part2(input)
    }.checkAnswer(13912)
}

fun part1(input: String): Int {
    val (order, boards) = parseInput(input)

    for (index in order.indices) {
        val draw = order.slice(0..index)
        val winners = boards.find { board -> board.isWinning(draw) }
        if (winners != null) {
            return winners.score(draw)
        }
    }

    throw Exception("Didn't get an answer :(")
}

fun part2(input: String): Int? {
    val (order, boards) = parseInput(input)

    return boards
        .map { board ->
            for (index in (order.indices)) {
                val draw = order.slice(0..index)
                if (board.isWinning(draw)) return@map Pair(board, draw)
            }
            throw Exception("board never completed?!")
        }
        .maxByOrNull { (_, winningDraw) -> winningDraw.size }
        ?.let { (board, winningDraw) -> board.score(winningDraw) }
}

fun parseInput(input: String): Pair<List<Int>, List<BingoCard>> {
    val blocks = input.split("\n\n").map { it.trim() }
    val draw = blocks[0].split(",").map { it.toInt() }
    val boards = blocks.drop(1).map { block ->
        block
            .replace("\n", " ")
            .replace("  ", " ")
            .split(" ")
            .map { it.toInt() }
            .let(::BingoCard)
    }
    return Pair(draw, boards)
}

data class BingoCard(
    private val numbers: List<Int>,
) {
    fun isWinning(draw: List<Int>): Boolean =
        (rows + cols).find { it.all { number -> number in draw } } != null

    fun score(draw: List<Int>): Int {
        val uncalled = numbers.filterNot { number -> number in draw }
        return uncalled.sum() * draw.last()
    }

    private val rows: List<List<Int>> = numbers.windowed(5, 5)
    private val cols: List<List<Int>> = rows.flip()
}