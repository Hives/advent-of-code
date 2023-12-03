package days.day02

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day02/input.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(2204)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(71036)
}

fun part1(input: List<String>): Int {
    val maxRed = 12
    val maxGreen = 13
    val maxBlue = 14
    return input.map(::parse).sumOf { (id, sets) ->
        val viable = sets.all {
            it.getOrDefault("red", 0) <= maxRed &&
                    it.getOrDefault("green", 0) <= maxGreen &&
                    it.getOrDefault("blue", 0) <= maxBlue
        }
        if (viable) id else 0
    }
}

fun part2(input: List<String>) =
    input.map(::parse).sumOf { (_, sets) ->
        val minRed = sets.maxOf { it.getOrDefault("red", 0) }
        val minGreen = sets.maxOf { it.getOrDefault("green", 0) }
        val minBlue = sets.maxOf { it.getOrDefault("blue", 0) }
        minRed * minGreen * minBlue
    }

fun parse(line: String): Pair<Int, List<Map<String, Int>>> {
    val (game, sets) = line.split(": ")
    val id = game.split(" ")[1].toInt()
    val picks = sets.split("; ").map { set ->
        set.split(", ").associate {
            val (count, colour) = it.split(" ")
            colour to count.toInt()
        }
    }
    return id to picks
}
