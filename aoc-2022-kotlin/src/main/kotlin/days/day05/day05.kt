package days.day05

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day05.txt").string()
    val exampleInput = Reader("day05-example.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer("NTWZZWHFV")

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer("BRZGFVBTJ")
}

fun part1(input: String): String {
    val (state, moves) = parse(input)
    moves.forEach { move ->
        repeat(move.count) {
            state[move.to].add(state[move.from].removeLast())
        }
    }
    return state.map { it.last() }.joinToString("")
}

fun part2(input: String): String {
    val (state, moves) = parse(input)
    moves.forEach { move ->
        val stack = mutableListOf<Char>()
        repeat(move.count) {
            stack.add(state[move.from].removeLast())
        }
        stack.reversed().forEach { state[move.to].add(it) }
    }
    return state.map { it.last() }.joinToString("")
}

fun parse(input: String): Pair<List<MutableList<Char>>, List<Move>> {
    val (configLines, movesLines) = input.split("\n\n")

    val stackCount = configLines.last().toString().toInt()
    val stacks = List(stackCount) { mutableListOf<Char>() }
    configLines.lines().dropLast(1).reversed().forEach { row ->
        row.forEachIndexed { index, c ->
            if (c.isLetter()) stacks[(index - 1) / 4].add(c)
        }
    }

    val moves = movesLines.lines().mapNotNull {
        val match = Regex("""move (\d+) from (\d+) to (\d+)""").find(it)
        match?.destructured?.let { (a, b, c) -> Move(count = a.toInt(), from = b.toInt() - 1, to = c.toInt() - 1) }
    }

    return Pair(stacks, moves)
}

data class Move(val count: Int, val from: Int, val to: Int)

fun List<List<Char>>.flipNotSquare(): List<List<Char>> {
    val cols = this.maxOfOrNull { it.size }!! - 1
    return (0..cols).map { col ->
        (this.indices).map { row ->
            this[row].getOrElse(col) { ' ' }
        }
    }
}
