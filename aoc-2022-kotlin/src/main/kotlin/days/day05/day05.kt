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
    moves.forEach { it.applyPt1(state) }
    return state.getEnds()
}

fun part2(input: String): String {
    val (state, moves) = parse(input)
    moves.forEach { it.applyPt2(state) }
    return state.getEnds()
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

data class Move(val count: Int, val from: Int, val to: Int) {
    fun applyPt1(state: List<MutableList<Char>>) {
        repeat(count) {
            state[to].add(state[from].removeLast())
        }
    }

    fun applyPt2(state: List<MutableList<Char>>) {
        val stack = mutableListOf<Char>()
        repeat(count) {
            stack.add(state[from].removeLast())
        }
        stack.reversed().forEach { state[to].add(it) }
    }
}

fun Iterable<Iterable<Char>>.getEnds() = map(Iterable<Char>::last).joinToString("")
