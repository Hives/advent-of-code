package days.day17

import lib.CompassDirection
import lib.Reader
import lib.Vector
import lib.checkAnswer

fun main() {
    val input = Reader("day17.txt").chars()
    val exampleInput = Reader("day17-example.txt").chars()

    part1(input).checkAnswer(3067L)
}

fun part1(input: List<Char>): Long =
    findHeight(input, 2022)

//fun part2(input: List<Char>) {
//    val repeatTime = input.size * 5
//    val heightAfterFirstRepeat = findHeight(input, repeatTime)
//    println(heightAfterFirstRepeat)
//}

fun findHeight(input: List<Char>, iterations: Int): Long {
    val landed = (0L..6L).map { VectorL(it, 0) }.toMutableSet()
    val rockRepeater = Repeater(rocks)
    val jetRepeater = Repeater(parse(input))

    fun applyNextJet(rock: Set<VectorL>): Set<VectorL> {
        val jet = jetRepeater.next()
        val newRock = rock.translate(jet)
        return if (
            newRock.minOf { it.x } >= 0 &&
            newRock.maxOf { it.x } < 7 &&
            newRock.intersect(landed).isEmpty()
        ) newRock
        else rock
    }

    fun moveDown(rock: Set<VectorL>): Set<VectorL> {
        val newRock = rock.translate(VectorL(0, -1))
        return if (newRock.intersect(landed).isEmpty()) newRock
        else rock
    }

    fun findRestingPlace(rock: Set<VectorL>): Set<VectorL> {
        val jettedRock = applyNextJet(rock)
        val downedRock = moveDown(jettedRock)
        return if (downedRock == jettedRock) downedRock
        else findRestingPlace(downedRock)
    }

    fun placeNextRock() {
        val topLandedY = landed.maxOf { it.y }
        val initialOffset = VectorL(2, topLandedY + 4)
        val rock = rockRepeater.next().translate(initialOffset)

        val restingPlace = findRestingPlace(rock)
        landed.addAll(restingPlace)
    }

    repeat(iterations) {
        placeNextRock()
    }

    return landed.maxOf { it.y }.toLong()
}

fun parse(input: List<Char>) =
    input.map {
        when (it) {
            '>' -> VectorL(1, 0)
            '<' -> VectorL(-1, 0)
            else -> throw Exception("Bad direction: $it")
        }
    }

fun Set<VectorL>.translate(v: VectorL) = map { it + v }.toSet()

class Repeater<T>(private val items: List<T>) {
    var i = -1
    fun next(): T {
        i = (i + 1) % items.size
        return items[i]
    }
}

val rocks = listOf(
    setOf(VectorL(0, 0), VectorL(1, 0), VectorL(2, 0), VectorL(3, 0)),
    setOf(VectorL(1, 0), VectorL(0, 1), VectorL(1, 1), VectorL(2, 1), VectorL(1, 2)),
    setOf(VectorL(0, 0), VectorL(1, 0), VectorL(2, 0), VectorL(2, 1), VectorL(2, 2)),
    setOf(VectorL(0, 0), VectorL(0, 1), VectorL(0, 2), VectorL(0, 3)),
    setOf(VectorL(0, 0), VectorL(0, 1), VectorL(1, 0), VectorL(1, 1)),
)

fun Collection<Vector>.print() {
    println()
    val xRange = this.minOf { it.x }..this.maxOf { it.x }
    val yRange = this.minOf { it.y }..this.maxOf { it.y }
    val grid = MutableList(yRange.last - yRange.first + 1) {
        MutableList(xRange.last - xRange.first + 1) {
            '.'
        }
    }
    this.forEach {
        grid[it.y - yRange.first][it.x - xRange.first] = '#'
    }
    grid.reversed().forEach { println(it.joinToString("")) }
}

data class VectorL(val x: Long, val y: Long) {
    operator fun plus(other: VectorL) = VectorL(
        x = this.x + other.x,
        y = this.y + other.y
    )
}
