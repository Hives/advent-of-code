package days.day08

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day08.txt").strings().map { it.map(Char::digitToInt) }
    val exampleInput = Reader("day08-example.txt").strings().map { it.map(Char::digitToInt) }

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1787)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(440640)
}

fun part1(input: List<List<Int>>): Int {
    val visible = mutableSetOf<Pair<Int, Int>>()

    // from left
    (input.indices).forEach { y ->
        (input[y].indices).forEach { x ->
            val tree = input[y][x]
            val potentialBlockers = input[y].subList(0, x)
            if (potentialBlockers.all { it < tree }) {
                visible.add(Pair(x, y))
            }
        }
    }

    // from right
    (input.indices).forEach { y ->
        ((input[y].size - 1) downTo 0).forEach { x ->
            val tree = input[y][x]
            val potentialBlockers = input[y].subList(x + 1, input[y].size)
            if (potentialBlockers.all { it < tree }) {
                visible.add(Pair(x, y))
            }
        }
    }

    // from top
    (input[0].indices).forEach { x ->
        (input.indices).forEach inner@{ y ->
            val tree = input[y][x]
            val potentialBlockers = (0 until y).map { it -> input[it][x] }
            if (potentialBlockers.all { it < tree }) {
                visible.add(Pair(x, y))
            }
        }
    }

    // from bottom
    (input[0].indices).forEach { x ->
        ((input.size - 1) downTo 0).forEach inner@{ y ->
            val tree = input[y][x]
            val potentialBlockers = ((input.size - 1) downTo (y + 1)).map { input[it][x] }
            if (potentialBlockers.all { it < tree }) {
                visible.add(Pair(x, y))
            }
        }
    }

    return visible.size
}

fun part2(input: List<List<Int>>): Int =
    input.indices.map { y -> input[y].indices.map { x -> Pair(x, y) } }.flatten()
        .maxOf { point -> point.scenicScore(input) }

fun Pair<Int, Int>.scenicScore(map: List<List<Int>>): Int {
    val tree = map[second][first]

    // up
    val up = run {
        var count = 1
        var current = this.up()
        while (current.second >= 0 && map[current.second][current.first] < tree) {
            current = current.up()
            count += 1
        }
        if (current.second < 0) count -= 1
        count
    }

    // left
    val left = run {
        var count = 1
        var current = this.left()
        while (current.first >= 0 && map[current.second][current.first] < tree) {
            current = current.left()
            count += 1
        }
        if (current.first < 0) count -= 1
        count
    }

    // down
    val down = run {
        var count = 1
        var current = this.down()
        while (current.second < map.size && map[current.second][current.first] < tree) {
            current = current.down()
            count += 1
        }
        if (current.second == map.size) count -= 1
        count
    }

    // right
    val right = run {
        var count = 1
        var current = this.right()
        while (current.first < map[0].size && map[current.second][current.first] < tree) {
            current = current.right()
            count += 1
        }
        if (current.first == map[0].size) count -= 1
        count
    }

    return up * left * right * down
}

fun Pair<Int, Int>.up() = Pair(first, second - 1)
fun Pair<Int, Int>.down() = Pair(first, second + 1)
fun Pair<Int, Int>.left() = Pair(first - 1, second)
fun Pair<Int, Int>.right() = Pair(first + 1, second)
