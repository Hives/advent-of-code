package days.day25

import dev.forkhandles.tuples.Tuple4
import lib.CompassDirections
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("day25.txt").strings()
    val exampleInput = Reader("day25-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 5, iterations = 10) {
        part1(input)
    }.checkAnswer(417)
}

fun part1(input: List<String>): Int {
    val (downers, righters, maxX, maxY) = parse(input)

    tailrec fun go(downers: Set<Vector>, righters: Set<Vector>, step: Int = 1): Int {
        val (newDowners, newRighters) = step(downers, righters, maxX, maxY)
        return if (newDowners == downers && newRighters == righters) step
        else go(newDowners, newRighters, step + 1)
    }

    return go(downers, righters)
}

fun step(downers: Set<Vector>, righters: Set<Vector>, maxX: Int, maxY: Int): Pair<Set<Vector>, Set<Vector>> {
    val all = downers + righters

    val (rightersCanMove, rightersCantMove) = righters.partition { righter ->
        righter.moveRightLooping(maxX) !in all
    }
    val movedRighters = rightersCanMove.map { it.moveRightLooping(maxX) }
    val finalRighters = rightersCantMove + movedRighters

    val newAll = downers + finalRighters

    val (downersCanMove, downersCantMove) = downers.partition { downer ->
        downer.moveDownLooping(maxY) !in newAll
    }
    val movedDowners = downersCanMove.map { it.moveDownLooping(maxY) }
    val finalDowners = downersCantMove + movedDowners

    return Pair(finalDowners.toSet(), finalRighters.toSet())
}

fun Vector.moveRightLooping(maxX: Int) = this.copy(x = (x + 1) % maxX)
fun Vector.moveDownLooping(maxY: Int) = this.copy(y = (y + 1) % maxY)

fun parse(input: List<String>): Tuple4<Set<Vector>, Set<Vector>, Int, Int> {
    val chars = input.map { it.toList() }
    val downers = chars.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell ->
            if (cell == 'v') Vector(x, y) else null
        }
    }.toSet()
    val righters = chars.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell ->
            if (cell == '>') Vector(x, y) else null
        }
    }.toSet()
    val maxX = chars.first().size
    val maxY = chars.size
    return Tuple4(downers, righters, maxX, maxY)
}
