package days.day17

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day17.txt").chars()
    val exampleInput = Reader("day17-example.txt").chars()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(3_067L)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(1_514_369_501_484L)
}

fun part1(input: List<Char>): Long =
    findHeight(input, 2022)

fun part2(input: List<Char>): Long =
    findHeight(input, 1_000_000_000_000L)

fun findHeight(input: List<Char>, iterations: Long): Long {
    val numberOfRowsToKeep = 40

    var landed = (0..6).map { Vector(it, 0) }.toMutableSet()
    var offset = 0L
    var rockCount = 0
    val states = mutableMapOf<State, Pair<Int, Long>>()

    var rockIndex = -1
    fun nextRock(): Set<Vector> {
        rockIndex = (rockIndex + 1) % rocks.size
        return rocks[rockIndex]
    }

    val jets = parse(input)
    var jetIndex = -1
    fun nextJet(): Vector {
        jetIndex = (jetIndex + 1) % jets.size
        return jets[jetIndex]
    }

    fun discardLowerRows() {
        val topY = landed.maxOf { it.y }
        landed.removeIf { it.y < topY - numberOfRowsToKeep }
        val minY = landed.minOf { it.y }
        offset += minY
        landed = landed.map { it - Vector(0, minY) }.toMutableSet()
    }

    fun applyNextJet(rock: Set<Vector>): Set<Vector> {
        val jet = nextJet()
        val newRock = rock.translate(jet)
        return if (
            newRock.minOf { it.x } >= 0 &&
            newRock.maxOf { it.x } < 7 &&
            newRock.intersect(landed).isEmpty()
        ) newRock
        else rock
    }

    fun moveDown(rock: Set<Vector>): Set<Vector> {
        val newRock = rock.translate(Vector(0, -1))
        return if (newRock.intersect(landed).isEmpty()) newRock
        else rock
    }

    tailrec fun findRestingPlace(rock: Set<Vector>): Set<Vector> {
        val jettedRock = applyNextJet(rock)
        val downedRock = moveDown(jettedRock)
        return if (downedRock == jettedRock) downedRock
        else findRestingPlace(downedRock)
    }

    fun placeNextRock() {
        val maxLandedY = landed.maxOf { it.y }
        val initialOffset = Vector(2, maxLandedY + 4)
        val rock = nextRock().translate(initialOffset)

        val restingPlace = findRestingPlace(rock)
        landed.addAll(restingPlace)

        discardLowerRows()

        rockCount++
    }

    tailrec fun findLoop(): Pair<Int, Int> {
        placeNextRock()
        val state = State(
            rockIndex = rockIndex,
            jetIndex = jetIndex,
            rocks = landed.toSet()
        )
        return if (state in states) {
            val loopStart = states[state]!!.first
            val loopEnd = rockCount
            Pair(loopStart, loopEnd)
        } else {
            states[state] = Pair(rockCount, offset)
            findLoop()
        }
    }

    val (loopStart, loopEnd) = findLoop()

    val finalIndex = (iterations - loopStart) % (loopEnd - loopStart) + loopStart
    val loopIterations = (iterations - loopStart) / (loopEnd - loopStart)

    val loopStartOffset = states.values.first { it.first == loopStart }.second
    val loopEndOffset = offset
    val offsetIncreaseOverOneLoop = loopEndOffset - loopStartOffset
    val offsetIncreaseToFinalIndex = states.values.first { it.first == finalIndex.toInt() }.second - loopStartOffset

    return loopStartOffset + (offsetIncreaseOverOneLoop * loopIterations) + offsetIncreaseToFinalIndex + landed.maxOf { it.y }
}

data class State(val rockIndex: Int, val jetIndex: Int, val rocks: Set<Vector>)

fun parse(input: List<Char>) =
    input.map {
        when (it) {
            '>' -> Vector(1, 0)
            '<' -> Vector(-1, 0)
            else -> throw Exception("Bad direction: $it")
        }
    }

fun Set<Vector>.translate(v: Vector) = map { it + v }.toSet()

val rocks = listOf(
    setOf(Vector(0, 0), Vector(1, 0), Vector(2, 0), Vector(3, 0)),
    setOf(Vector(1, 0), Vector(0, 1), Vector(1, 1), Vector(2, 1), Vector(1, 2)),
    setOf(Vector(0, 0), Vector(1, 0), Vector(2, 0), Vector(2, 1), Vector(2, 2)),
    setOf(Vector(0, 0), Vector(0, 1), Vector(0, 2), Vector(0, 3)),
    setOf(Vector(0, 0), Vector(0, 1), Vector(1, 0), Vector(1, 1)),
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
