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

    time(message = "Part 1", warmUp = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(188398)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: Grid<Char>): Int {
    input.sumOf { evaluate(it).also(::println) }.also(::println)
    return -1
}

fun part2(input: Grid<Char>): Int {
    return -1
}

fun evaluate(input: List<Char>): Int {
    val presses = getUltimatePresses(input)
    return presses.size * input.dropLast(1).joinToString("").toInt()
}

fun getUltimatePresses(input: List<Char>): List<Char> {
    val dpad1Presses = buttonsToDirections(input, numberPad, Vector(2, 3))
    val dpad2Presses = dpad1Presses.flatMap {
        buttonsToDirections(it, directionPad, Vector(2, 0))

    }
    val dpad3Presses = dpad2Presses.flatMap {
        buttonsToDirections(it, directionPad, Vector(2, 0))
    }
    return dpad3Presses.minBy { it.size }
}

fun buttonsToDirections(
    buttons: List<Char>,
    pad: Pad,
    initial: Vector
): List<List<Char>> {
    val positions = listOf(initial) + buttons.map { pad.buttons[it]!! }
    return positions.windowed(2).fold(listOf(emptyList())) { acc, (a, b) ->
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
    val horizontalSteps = abs(v.x)
    val verticalDirection = when {
        v.y > 0 -> Vector(0, 1)
        else -> Vector(0, -1)
    }
    val verticalSteps = abs(v.y)
    return (List(horizontalSteps) { horizontalDirection } + List(verticalSteps) { verticalDirection }).permutations()
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
    verboten = Vector(0, 3)
)

val directionPad = Pad(
    buttons = mapOf(
        '^' to Vector(1, 0),
        'A' to Vector(2, 0),
        '<' to Vector(0, 1),
        'v' to Vector(1, 1),
        '>' to Vector(2, 1),
    ),
    verboten = Vector(0, 0)
)

data class Pad(val buttons: Map<Char, Vector>, val verboten: Vector)
