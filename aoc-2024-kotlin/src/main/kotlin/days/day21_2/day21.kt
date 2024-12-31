package days.day21_2

import kotlin.system.exitProcess
import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day21/input.txt").grid()
    val exampleInput = Reader("/day21/example-1.txt").grid()

    part1(exampleInput).checkAnswer(126384)
    part1(input).checkAnswer(188398)
    part2(input).checkAnswer(-1)

    exitProcess(0)

    time(message = "Part 1", warmUp = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(188398)

//    time(message = "Part 2") {
//        part2(input)
//    }.checkAnswer(0)
}

fun part1(input: Grid<Char>) =
    input.sumOf { getComplexity(it, 2) }

fun part2(input: Grid<Char>) =
    input.sumOf { getComplexity(it, 25) }

fun getComplexity(code: List<Char>, numPadDepth: Int): Long =
    getNumericPart(code) * getTotalKeyPresses(code, numPadDepth)

fun getTotalKeyPresses(code: List<Char>, numPadDepth: Int): Long {
    val numPadKeyPresses = getNumPadKeyPresses(code)
    return getDPadKeyPresses(numPadKeyPresses, numPadDepth)
}

fun getNumPadKeyPresses(code: List<Char>): List<Char> =
    (listOf('A') + code)
        .windowed(2)
        .fold(emptyList()) { acc, (start, target) ->
            acc + numberPad.getShortestKeysForPress(start, target)
        }

fun getDPadKeyPresses(code: List<Char>, numPadDepth: Int) =
    (listOf('A') + code)
        .windowed(2)
        .fold(0L) { acc, (start, target) ->
            acc + getDPadFragmentKeyPresses(start, target, numPadDepth)
        }

val dPadFragmentKeyPressesCache = mutableMapOf<Triple<Char, Char, Int>, Long>()

fun getDPadFragmentKeyPresses(start: Char, target: Char, depth: Int): Long {
    val cacheKey = Triple(start, target, depth)
    if (cacheKey in dPadFragmentKeyPressesCache) return dPadFragmentKeyPressesCache[cacheKey]!!

    val keys = directionPad.getShortestKeysForPress(start, target)
    val result = if (depth == 1) keys.size.toLong()
    else {
        (listOf('A') + keys)
            .windowed(2)
            .fold(0L) { acc, (start, target) ->
                acc + getDPadFragmentKeyPresses(start, target, depth - 1)
            }
    }

    dPadFragmentKeyPressesCache[cacheKey] = result

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
    private val shortestKeysCache = mutableMapOf<Pair<Char, Char>, List<Char>>()

    fun getShortestKeysForPress(start: Char, target: Char): List<Char> {
        val cacheKey = Pair(start, target)
        if (cacheKey in shortestKeysCache) return shortestKeysCache[cacheKey]!!

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
        val pathToTarget =
            if (cornerXsFirst != verboten) (xs + ys)
            else (ys + xs)

        val presses = pathToTarget + 'A'

        shortestKeysCache[cacheKey] = presses

        return presses
    }
}
