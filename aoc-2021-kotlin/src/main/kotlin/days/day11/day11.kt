package days.day11

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day11.txt").strings()
    val exampleInput = Reader("day11-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1640)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(312)
}

fun part1(input: List<String>): Int {
    tailrec fun go(octopuses: Octopuses, flashCount: Int, steps: Int): Int =
        if (steps == 0) flashCount
        else {
            val (newOctopuses, newFlashes) = step(octopuses)
            go(newOctopuses, flashCount + newFlashes, steps - 1)
        }

    return go(Octopuses(parse(input)), 0, 100)
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

fun step(octopuses: Octopuses): Pair<Octopuses, Int> {
    tailrec fun flash(octopuses: Octopuses, flashed: Set<Vector>): Pair<Octopuses, Int> {
        val flashables = octopuses.flashables(flashed)

        return if (flashables.isEmpty()) Pair(octopuses, flashed.size)
        else {
            val flashableAdjacent = flashables.flatMap { it.surrounding }
            flash(octopuses.increment(flashableAdjacent), flashed + flashables)
        }
    }

    val incremented = octopuses.incrementAll()

    val (afterFlash, flashedCount) = flash(incremented, emptySet())

    return Pair(afterFlash.resetFlashed(), flashedCount)
}

data class Octopuses(private val cells: List<List<Int>>) {
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

    fun flashables(alreadyFlashed: Set<Vector>) =
        cells.mapIndexed { y, row ->
            row.mapIndexedNotNull { x, cell ->
                if (cell > 9) Vector(x, y)
                else null
            }
        }
            .flatten()
            .let { it - alreadyFlashed }
            .toSet()

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
