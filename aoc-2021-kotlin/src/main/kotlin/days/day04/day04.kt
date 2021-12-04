package days.day04

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val exampleInput = Reader("day04-example.txt").string()
    val input = Reader("day04.txt").string()

    time {
        part1(input)
    }.checkAnswer(72770)

    time {
        part2(input)
    }.checkAnswer(13912)
}

fun part1(input: String): Int {
    val (order, boards) = parseInput(input)
    (order.indices).forEach {
        val draw = order.slice(0..it)
        val winners = boards.find { board -> board.isWinning(draw) }
        if (winners != null) {
            return winners.score(draw)
        }
    }

    throw Exception("Didn't get an answer :(")
}

fun part2(input: String): Int {
    val (order, boards) = parseInput(input)
    lateinit var loser: Board

    (order.indices.reversed()).forEach {
        val draw = order.slice(0..it)
        val losers = boards.filter { board -> !board.isWinning(draw) }
        if (losers.size == 1) {
            loser = losers.single()
            return@forEach
        }
    }

    (order.indices).forEach {
        val draw = order.slice(0..it)
        if (loser.isWinning(draw)) {
            return loser.score(draw)
        }
    }

    throw Exception("Didn't get an answer :(")
}

fun parseInput(input: String): Pair<List<Int>, List<Board>> {
    val blocks = input.split("\n\n")
    val draw = blocks[0].trim().split(",").map { it.toInt() }
    val boards = blocks.drop(1).map { block ->
        block.trim()
            .replace("\n", " ")
            .replace("  ", " ")
            .split(" ")
            .map { it.toInt() }
            .let { Board(it) }
    }
    return Pair(draw, boards)
}

data class Board(
    private val numbers: List<Int>,
) {
    fun isWinning(draw: List<Int>): Boolean =
        (rows + cols).find { it.all { number -> number in draw } } != null

    fun score(draw: List<Int>): Int {
        val uncalled = numbers.filterNot { number -> number in draw }
        return uncalled.sum() * draw.last()
    }

    private val rows: List<List<Int>> =
        (0..4).map { row -> (0..4).map { col -> get(row, col) } }
    private val cols: List<List<Int>> =
        (0..4).map { col -> (0..4).map { row -> get(row, col) } }

    private fun get(row: Int, col: Int) = numbers[row * 5 + col]
}