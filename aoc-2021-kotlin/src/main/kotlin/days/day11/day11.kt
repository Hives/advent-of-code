package days.day11

import lib.Reader
import lib.Vector

fun main() {
    val input = Reader("day11.txt").strings()
    val exampleInput = Reader("day11-example.txt").strings()

    println(part1(input))
}

fun part1(input: List<String>): Int {
    tailrec fun go(octopuses: Octopuses, flashes: Int, steps: Int): Int =
        if (steps == 0) flashes
        else {
            val (newOctopuses, newFlashes) = step(octopuses)
            go(newOctopuses, flashes + newFlashes, steps - 1)
        }

    val initial = Octopuses(parse(input))

    return go(initial, 0, 100)
}

fun step(octopuses: Octopuses): Pair<Octopuses, Int> {
    tailrec fun flash(octopuses: Octopuses, flashed: Set<Vector>): Pair<Octopuses, Int> {
//        octopuses.pp()
//        println(flashed)
//        println(flashed.size)

        val flashables = octopuses.flashables(flashed)
        return if (flashables.isEmpty()) {
//            println("returning with ${flashed.size}")
            Pair(octopuses, flashed.size)
        } else {
            val flashableAdjacent = flashables.flatMap { it.surrounding }
            flash(octopuses.increment(flashableAdjacent), flashed + flashables)
        }
    }

    val incremented = octopuses.incrementAll()

    val (final, flashedCount) = flash(incremented, emptySet())

    return Pair(final.resetFlashed(), flashedCount)
}

fun parse(input: List<String>) = input.map { row -> row.toList().map { cell -> cell.digitToInt() } }

data class Octopuses(private val cells: List<List<Int>>) {
    private val cols = cells[0].size
    private val rows = cells.size

    fun incrementAll() = Octopuses(cells.map { row -> row.map { cell -> cell + 1 } })

    fun increment(some: List<Vector>): Octopuses {
//        println("incrementing:")
//        println(some)
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

    fun pp() {
        println("---")
        cells.forEach { row ->
            row.map { cell ->
                if (cell > 9) "*"
                else cell.toString()
            }.also {
                println(it.joinToString(""))
            }
        }
    }
}

private val example2 = """11111
19991
19191
19991
11111""".lines()
