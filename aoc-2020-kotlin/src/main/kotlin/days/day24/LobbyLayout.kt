package days.day24

import days.day24.Direction.E
import days.day24.Direction.NE
import days.day24.Direction.NW
import days.day24.Direction.SE
import days.day24.Direction.SW
import days.day24.Direction.W

tailrec fun play(state: Set<Hex>, rounds: Int): Set<Hex> =
    if (rounds == 0) state
    else play(oneRound(state), rounds - 1)

fun oneRound(blackTiles: Set<Hex>): Set<Hex> {
    val newBlackTiles = mutableSetOf<Hex>()

    val allNeighbours = blackTiles.flatMap { it.neighbours() }.toSet()

    allNeighbours.forEach { tile ->
        val blackNeighbours = tile.neighbours().count { neighbour -> neighbour in blackTiles }

        if (tile in blackTiles) {
            if (blackNeighbours == 1 || blackNeighbours == 2) newBlackTiles.add(tile)
        } else {
            if (blackNeighbours == 2) newBlackTiles.add(tile)
        }
    }

    return newBlackTiles.toSet()
}

fun part1(input: List<String>): MutableSet<Hex> {
    val tiles = input.map { parseDirections(it) }.map { getDestination(it) }

    val blackTiles = mutableSetOf<Hex>()

    tiles.forEach {
        if (it in blackTiles) blackTiles.remove(it)
        else blackTiles.add(it)
    }

    return blackTiles
}

fun getDestination(directions: List<Direction>): Hex {
    tailrec fun go(directions: List<Direction>, current: Hex): Hex =
        if (directions.isEmpty()) current
        else go(directions.drop(1), current.neighbour(directions.first()))

    return go(directions, Hex(0, 0))
}

data class Hex(val x: Int, val y: Int) {
    private val rowIsEven: Boolean
        get() = y % 2 == 0

    fun neighbour(direction: Direction) =
        when (direction) {
            E -> Hex(x + 1, y)
            SE -> if (rowIsEven) Hex(x, y + 1) else Hex(x + 1, y + 1)
            SW -> if (rowIsEven) Hex(x - 1, y + 1) else Hex(x, y + 1)
            W -> Hex(x - 1, y)
            NE -> if (rowIsEven) Hex(x, y - 1) else Hex(x + 1, y - 1)
            NW -> if (rowIsEven) Hex(x - 1, y - 1) else Hex(x, y - 1)
        }

    fun neighbours() = Direction.values().map { neighbour(it) }
}

fun parseDirections(input: String): List<Direction> {
    tailrec fun go(input: String, directions: List<Direction>): List<Direction> =
        if (input.isEmpty()) directions
        else {
            val direction = Direction.values()
                .single { input.startsWith(it.name.toLowerCase()) }

            go(input.drop(direction.name.length), directions + direction)
        }

    return go(input, emptyList())
}

enum class Direction {
    E, SE, SW, W, NW, NE
}