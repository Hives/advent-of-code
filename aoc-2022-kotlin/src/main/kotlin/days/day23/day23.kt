package days.day23

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day23.txt").grid()
    val exampleInput = Reader("day23-example.txt").grid()
    val smallExample = Reader("day23-small-example.txt").grid()

    time(message = "Part 1", warmUpIterations = 5, iterations = 10) {
        part1(input)
    }.checkAnswer(3689)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        // slow: ~15s
        part2(input)
    }.checkAnswer(965)
}

fun part1(input: List<List<Char>>): Int {
    val initial = parse(input)

    tailrec fun go(elves: Set<Vector>, round: Int): Set<Vector> =
        if (round == 10) elves
        else {
            val newElves = oneMove(elves, round)
            go(newElves, round + 1)
        }

    return go(initial, 0).countEmptySquaresInSmallestGrid()
}

fun part2(input: List<List<Char>>): Int {
    val initial = parse(input)

    tailrec fun go(elves: Set<Vector>, round: Int): Int =
        when (val newElves = oneMove(elves, round)) {
            elves -> round + 1
            else -> go(newElves, round + 1)
        }

    return go(initial, 0)
}

fun oneMove(elves: Set<Vector>, round: Int): Set<Vector> {
    val proposedMoves = proposeMoves(elves, round)
    return resolveMoves(proposedMoves)
}

fun proposeMoves(elves: Set<Vector>, round: Int): List<Pair<Vector, Vector>> =
    elves.map { elf ->
        val surrounding = elf.surrounding()
        if (surrounding.none { it in elves }) Pair(elf, elf)
        else {
            val proposedMove = (0 until 4).asSequence().map { n ->
                val directionToCheck = fourDirections[(round + n) % 4]
                if (directionToCheck.none { elf + it in elves }) elf + directionToCheck.first() else null
            }.firstOrNull {
                it != null
            }

            if (proposedMove == null) Pair(elf, elf) else Pair(elf, proposedMove)
        }
    }

fun resolveMoves(proposals: List<Pair<Vector, Vector>>): Set<Vector> {
    val proposedLocations = proposals.map { it.second }
    return proposals.map { (current, proposed) ->
        if (proposedLocations.count { it == proposed } == 1) proposed
        else current
    }.toSet()
}

fun Set<Vector>.countEmptySquaresInSmallestGrid() =
    generateGrid().flatten().count { it == '.' }

fun Set<Vector>.generateGrid(): List<MutableList<Char>> {
    val maxX = maxOf { it.x }
    val minX = minOf { it.x }
    val maxY = maxOf { it.y }
    val minY = minOf { it.y }
    val grid = (minY..maxY).map { (minX..maxX).map { '.' }.toMutableList() }
    forEach { elf ->
        grid[elf.y - minY][elf.x - minX] = '#'
    }
    return grid.toList()
}

fun Set<Vector>.printy() {
    generateGrid().forEach { println(it.joinToString("")) }
}

fun parse(input: List<List<Char>>): Set<Vector> =
    input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell ->
            if (cell == '#') Vector(x, y) else null
        }
    }.toSet()

fun Vector.surrounding() = CompassDirection.values().map { this + it.vector }

enum class CompassDirection(val vector: Vector) {
    N(Vector(0, -1)),
    NE(Vector(1, -1)),
    E(Vector(1, 0)),
    SE(Vector(1, 1)),
    S(Vector(0, 1)),
    SW(Vector(-1, 1)),
    W(Vector(-1, 0)),
    NW(Vector(-1, -1))
}

val fourDirections = listOf(
    setOf(CompassDirection.N.vector, CompassDirection.NE.vector, CompassDirection.NW.vector),
    setOf(CompassDirection.S.vector, CompassDirection.SE.vector, CompassDirection.SW.vector),
    setOf(CompassDirection.W.vector, CompassDirection.NW.vector, CompassDirection.SW.vector),
    setOf(CompassDirection.E.vector, CompassDirection.NE.vector, CompassDirection.SE.vector),
)
