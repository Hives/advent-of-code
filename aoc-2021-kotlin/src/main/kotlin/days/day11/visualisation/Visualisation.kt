package days.day11.visualisation

import lib.Reader
import lib.Vector
import lib.checkAnswer

val frames = mutableListOf<MutableList<List<List<String>>>>()

fun main() {
    val input = Reader("day11.txt").strings()
    part2(input).checkAnswer(312)
    renderFrames(frames)
}

fun part2(input: List<String>): Int {
    tailrec fun go(octopuses: Octopuses, steps: Int): Int {
        val (newOctopuses, flashCount) = step(octopuses)

        return if (flashCount == octopuses.count) steps + 1
        else {
            go(newOctopuses, steps + 1)
        }
    }

    return go(Octopuses(parse(input)), 0)
}

fun parse(input: List<String>) = input.map { row -> row.toList().map { cell -> cell.digitToInt() } }

fun List<List<Int>>.format() = map { row -> row.map { cell -> if (cell > 9) "*" else "$cell" } }

fun step(octopuses: Octopuses): Pair<Octopuses, Int> {
    val stepFrames = mutableListOf<List<List<String>>>()

    tailrec fun flash(octopuses: Octopuses, flashed: Set<Vector>): Pair<Octopuses, Int> {
        stepFrames.add(octopuses.cells.format())

        val fullyCharged = octopuses.fullyCharged()

        return if (fullyCharged.isEmpty()) Pair(octopuses, flashed.size)
        else {
            val adjacent = fullyCharged.flatMap { it.surrounding } - flashed - fullyCharged
            flash(octopuses.resetFlashed().increment(adjacent), flashed + fullyCharged)
        }
    }

    val incremented = octopuses.incrementAll()

    val (afterFlash, flashedCount) = flash(incremented, emptySet())

    frames.add(stepFrames)

    return Pair(afterFlash, flashedCount)
}

data class Octopuses(val cells: List<List<Int>>) {
    private val cols = cells[0].size
    private val rows = cells.size

    val count: Int
        get() = rows * cols

    fun incrementAll() = Octopuses(cells.map { row -> row.map { cell -> cell + 1 } })

    fun increment(some: List<Vector>): Octopuses {
        val counts = some.groupingBy { it }.eachCount()

        return Octopuses(
            cells.mapIndexed { y, row ->
                row.mapIndexed { x, cell ->
                    counts[Vector(x, y)]?.let { count -> cell + count } ?: cell
                }
            }
        )
    }

    fun fullyCharged() =
        cells.mapIndexed { y, row ->
            row.mapIndexedNotNull { x, cell ->
                if (cell > 9) Vector(x, y)
                else null
            }
        }
            .flatten()

    fun resetFlashed() = Octopuses(cells.map { row ->
        row.map { cell ->
            if (cell > 9) 0
            else cell
        }
    })

    fun printy() {
        println("")
        cells.forEach { row ->
            println(row.joinToString(""))
        }
    }
}
