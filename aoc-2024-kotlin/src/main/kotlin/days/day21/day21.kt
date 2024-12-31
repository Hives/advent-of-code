package days.day21

import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day21/input.txt").grid()
    val exampleInput = Reader("/day21/example-1.txt").grid()

    time(message = "Part 1", warmUp = 10000, iterations = 100) {
        part1(input)
    }.checkAnswer(188398)

    time(message = "Part 2", warmUp = 10000, iterations = 100) {
        part2(input)
    }.checkAnswer(230049027535970)
}

fun part1(input: Grid<Char>) =
    input.sumOf { getComplexity(it, 2) }

fun part2(input: Grid<Char>) =
    input.sumOf { getComplexity(it, 25) }

fun getComplexity(code: List<Char>, numPadDepth: Int): Long =
    getNumericPart(code) * getTotalKeyPresses(code, numPadDepth)

fun getTotalKeyPresses(code: List<Char>, numPadDepth: Int) =
    getNumPadKeyPresses(code).minOf {
        getDPadKeyPressCount(it, numPadDepth)
    }

fun getNumPadKeyPresses(code: List<Char>): List<List<Char>> {
    return (listOf('A') + code)
        .windowed(2)
        .fold(listOf(emptyList())) { acc, (start, target) ->
            numberPad.getKeysForPress(start, target).flatMap { newPress ->
                acc.map { it + newPress }
            }.distinct()
        }
}

fun getDPadKeyPressCount(code: List<Char>, numPadDepth: Int) =
    (listOf('A') + code)
        .windowed(2)
        .fold(0L) { acc, (start, target) ->
            acc + getDPadFragmentKeyPressCount(start, target, numPadDepth)
        }

val dPadFragmentKeyPressCountCache = mutableMapOf<Triple<Char, Char, Int>, Long>()

fun getDPadFragmentKeyPressCount(start: Char, target: Char, depth: Int): Long {
    val cacheKey = Triple(start, target, depth)
    if (cacheKey in dPadFragmentKeyPressCountCache) return dPadFragmentKeyPressCountCache[cacheKey]!!

    val keysOptions = directionPad.getKeysForPress(start, target)
    val result =
        if (depth == 1) keysOptions.first().size.toLong()
        else {
            keysOptions.minOf { keys ->
                (listOf('A') + keys)
                    .windowed(2)
                    .fold(0L) { acc, (start, target) ->
                        acc + getDPadFragmentKeyPressCount(start, target, depth - 1)
                    }
            }
        }

    dPadFragmentKeyPressCountCache[cacheKey] = result

    return result
}

fun getNumericPart(code: List<Char>) =
    code.subList(0, 3).joinToString("").toInt()

val numberPad = Pad(
    buttons = mapOf(
        '7' to Vector(0, 0),
        '8' to Vector(1, 0),
        '9' to Vector(2, 0),
        '4' to Vector(0, 1),
        '5' to Vector(1, 1),
        '6' to Vector(2, 1),
        '1' to Vector(0, 2),
        '2' to Vector(1, 2),
        '3' to Vector(2, 2),
        '0' to Vector(1, 3),
        'A' to Vector(2, 3)
    ),
    verboten = Vector(0, 3),
)

val directionPad = Pad(
    buttons = mapOf(
        '^' to Vector(1, 0),
        'A' to Vector(2, 0),
        '<' to Vector(0, 1),
        'v' to Vector(1, 1),
        '>' to Vector(2, 1),
    ),
    verboten = Vector(0, 0),
)

data class Pad(val buttons: Map<Char, Vector>, val verboten: Vector) {
    private val keysCache = mutableMapOf<Pair<Char, Char>, List<List<Char>>>()

    fun getKeysForPress(start: Char, target: Char): List<List<Char>> {
        val cacheKey = Pair(start, target)
        if (cacheKey in keysCache) return keysCache[cacheKey]!!

        val startV = buttons[start]
        require(startV != null)
        val targetV = buttons[target]
        require(targetV != null)

        val xs = startV.pathTo(targetV.copy(y = startV.y)).drop(1).map {
            if (targetV.x < startV.x) '<' else '>'
        }
        val ys = startV.pathTo(targetV.copy(x = startV.x)).drop(1).map {
            if (targetV.y < startV.y) '^' else 'v'
        }

        val cornerXsFirst = Vector(targetV.x, startV.y)
        val cornerYsFirst = Vector(startV.x, targetV.y)

        val presses = when {
            cornerXsFirst == verboten -> listOf(ys + xs)
            cornerYsFirst == verboten -> listOf(xs + ys)
            else -> listOf(xs + ys, ys + xs)
        }.map { it + 'A' }

        keysCache[cacheKey] = presses

        return presses
    }
}
