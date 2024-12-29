package days.day21

import kotlin.math.abs
import kotlin.system.exitProcess
import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day21/input.txt").grid()
    val exampleInput = Reader("/day21/example-1.txt").grid()

    part2(exampleInput)

    exitProcess(0)

    time(message = "Part 1", warmUp = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(188398)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: Grid<Char>) =
    input.sumOf { evaluate(it, 3) }

fun part2(input: Grid<Char>): Int {
    getPressCount(input[0], 2)

    return -1
}

fun getPressCount(input: List<Char>, depth: Int): Long {
    println(input)
    val numPadPresses = buttonsToDirections(input, numberPad)[0]

    getDPadPressCount(numPadPresses, 1)

    return -1
}

fun getDPadPressCount(input: List<Char>, depth: Int): Long {
    println(input)

    val journeyPoints = listOf(directionPad.initial) + input.map { directionPad.buttons[it]!! }

    val c = journeyPoints.windowed(2).fold(0L) { acc, (start, end) ->
        acc + getPressCount(start, end, 2)
    }

    println(c)

    TODO()
}

fun evaluate(input: List<Char>, dpads: Int): Int {
    val presses = getUltimatePresses(input, dpads)
    return presses.size * input.dropLast(1).joinToString("").toInt()
}

fun getUltimatePresses(input: List<Char>, dpads: Int): List<Char> {
    val dpad1Presses = buttonsToDirections(input, numberPad)

    tailrec fun go(presses: List<List<Char>>, n: Int): List<List<Char>> {
        return if (n == 0) presses
        else {
            val foo = presses.flatMap {
                buttonsToDirections(it, directionPad)
            }
            go(foo, n - 1)
        }
    }

    val final = go(dpad1Presses, dpads - 1)

    return final.minBy { it.size }
}

fun buttonsToDirections(
    buttons: List<Char>,
    pad: Pad,
): List<List<Char>> {
    val positions = listOf(pad.initial) + buttons.map { pad.buttons[it]!! }
    return positions.windowed(2).fold(listOf(listOf())) { acc, (a, b) ->
        val paths = getPaths(a, b, pad.verboten)
        acc.flatMap { previous ->
            paths.map { path ->
                previous + path.map(::directionToChar) + 'A'
            }
        }
    }
}

fun directionToChar(d: Vector) =
    when (d) {
        Vector(1, 0) -> '>'
        Vector(-1, 0) -> '<'
        Vector(0, 1) -> 'v'
        Vector(0, -1) -> '^'
        else -> throw Exception("Invalid direction")
    }

val getPathCountCache = mutableMapOf<Triple<Vector, Vector, Int>, Long>()

fun getPressCount(
    initial: Vector,
    final: Vector,
    depth: Int
): Long {
    val verboten = Vector(0, 0)
    val diff = final - initial

    val path = getPaths(diff).filter { path ->
        var current = initial
        path.forEach { d ->
            current += d
            if (current == verboten) return@filter false
        }
        true
    }

    println(path)

    val result =
        if (depth == 1) {
            path.minOfOrNull { it.size + 1 }!!.toLong()
        }
        else {
            path.minOfOrNull {
                getPressCount(initial, final, depth - 1) + 1
            }!!
        }

    return result
}

fun getPaths(
    initial: Vector,
    final: Vector,
    verboten: Vector
): List<List<Vector>> {
    val diff = final - initial
    return getPaths(diff).filter { path ->
        var current = initial
        path.forEach { d ->
            current += d
            if (current == verboten) return@filter false
        }
        true
    }
}

fun getPaths(
    v: Vector
): Set<List<Vector>> {
    if (v == Vector(0, 0)) return setOf(emptyList())

    val horizontalDirection = when {
        v.x > 0 -> Vector(1, 0)
        else -> Vector(-1, 0)
    }
    val horizontalSteps = List(abs(v.x)) { horizontalDirection }
    val verticalDirection = when {
        v.y > 0 -> Vector(0, 1)
        else -> Vector(0, -1)
    }
    val verticalSteps = List(abs(v.y)) { verticalDirection }

    return setOf(
        horizontalSteps + verticalSteps,
        verticalSteps + horizontalSteps
    )

//    return (List(horizontalSteps) { horizontalDirection } + List(verticalSteps) { verticalDirection })
//        .permutations()
}

fun <T> List<T>.permutations(): Set<List<T>> =
    if (this.size == 1) setOf(this)
    else flatMapIndexed { index, x ->
        val xs = this.subList(0, index) + this.subList(index + 1, this.size)
        xs.permutations().map { listOf(x) + it }
    }.toSet()

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
    initial = Vector(2, 3)
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
    initial = Vector(2, 0)
)

data class Pad(val buttons: Map<Char, Vector>, val verboten: Vector, val initial: Vector)
