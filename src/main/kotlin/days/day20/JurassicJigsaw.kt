package days.day20

import days.day12.repeatedlyApply
import kotlin.math.sqrt

class Puzzle(input: String) {
    val tiles = parseInput(input)
    val dimension = sqrt(tiles.size.toDouble()).toInt()
    val placements = MutableList<OrientedTile?>(tiles.size) { null }
    val iterators = MutableList<Iterator<OrientedTile>?>(tiles.size) { null }

    var currentPosition = -1

    init {
        goToNextPosition()
    }

    fun go() {
        while (currentPosition < tiles.size) {
            crunch()
        }
    }

    fun multiplyCorners() =
        listOf(
            Coordinate(0, 0),
            Coordinate(0, dimension - 1),
            Coordinate(dimension - 1, 0),
            Coordinate(dimension - 1, dimension - 1)
        ).map {
            getPlacementFromCoordinates(it)?.tile?.id ?: 0
        }.reduce { a, b -> a * b }

    private fun crunch() {
        val possibility = findNextPossibility(currentPosition)

        if (possibility == null) {
            goBackAndIncrementAPreviousPlacement()
        } else {
            placements[currentPosition] = possibility
            goToNextPosition()
        }
    }

    private fun goToNextPosition() {
        currentPosition++
        if (currentPosition < tiles.size) {
            iterators[currentPosition] = OrientedTileIterator(getPossibilities())
        }
    }

    private fun goBackAndIncrementAPreviousPlacement() {
        while (!iterators[currentPosition]!!.hasNext()) {
            goToPreviousPosition()
        }
        iterators[currentPosition]!!.next()
    }

    private fun goToPreviousPosition() {
        iterators[currentPosition] = null
        currentPosition--
        placements[currentPosition] = null
    }

    private fun findNextPossibility(position: Int): OrientedTile? {
        while (iterators[position]!!.hasNext()) {
            val next = iterators[position]!!.next()
            if (next.alignsWithNeighbours(position)) return next
        }

        return null
    }

    private fun getPossibilities() = tiles.filterNot { it.isPlaced() }

    private fun Tile.isPlaced() = placements.map { it?.tile }.contains(this)

    private fun OrientedTile.alignsWithNeighbours(position: Int): Boolean =
        getNeighbouringPlacements(position).zip(0..3)
            .all { (neighbour, direction) -> this.alignsWith(neighbour, direction) }

    private fun getNeighbouringPlacements(position: Int): List<OrientedTile?> =
        convertPositionToCoordinates(position).neighbours.map { getPlacementFromCoordinates(it) }

    private fun convertPositionToCoordinates(position: Int): Coordinate =
        Coordinate(position / dimension, position % dimension)

    private fun getPlacementFromCoordinates(c: Coordinate): OrientedTile? =
        when {
            c.row < 0 -> null
            c.row >= dimension -> null
            c.col < 0 -> null
            c.col >= dimension -> null
            else -> placements.getOrNull(c.row * dimension + c.col)
        }

}

data class Coordinate(val row: Int, val col: Int) {
    val neighbours
        get() = listOf(
            Coordinate(row - 1, col),
            Coordinate(row, col + 1),
            Coordinate(row + 1, col),
            Coordinate(row, col - 1)
        )
}

fun parseInput(input: String) =
    input.trim().split("\n\n").map { Tile.from(it) }

// direction: 0 = top, 1 = right, 2 = bottom, 3 = left
fun OrientedTile.alignsWith(secondTile: OrientedTile?, direction: Int): Boolean =
    if (secondTile == null) true
    else this.border(direction) == secondTile.border((direction + 2) % 4)

data class OrientedTile(val tile: Tile, val orientation: Orientation) {
    fun border(direction: Int) = tile.borders(orientation)[direction]

    val image
        get(): List<String> =
            this.tile.rows
                .let { if (this.orientation.flipped) it.reflect() else it }
                .let { it.rotate(this.orientation.quarterTurnsCCW) }
                .let { tile ->
                    tile.dropFirstAndLast()
                        .map { row ->
                            row.dropFirstAndLast().joinToString("")
                        }
                }
}

fun List<OrientedTile?>.toImage(dimension: Int): List<String> =
    this
        .map { it!!.image }
        .chunked(dimension)
        .flatMap { chunk ->
            (chunk[0].indices).map { i -> chunk.joinToString("") { it[i] } }
        }


data class Tile(val id: Long, val rows: List<List<Char>>) {
//    override fun toString(): String {
//        return "T($id)"
//    }

    fun borders(orientation: Orientation): List<List<Char>> =
        rows.let { if (orientation.flipped) it.reflect() else it }
            .rotate(orientation.quarterTurnsCCW)
            .let { oriented ->
                // ordered top, right, bottom, left
                // top and bottom read left-to-right, left and right read top-to-bottom
                listOf(
                    oriented.first(),
                    oriented.map { it.last() },
                    oriented.last(),
                    oriented.map { it.first() }
                )
            }

    companion object {
        fun from(input: String): Tile {
            val (first, second) = input.split(":\n")
            return Tile(
                id = first.split(" ")[1].toLong(),
                rows = second.split("\n").map { row -> row.toList() },
            )
        }
    }
}

fun <T> List<List<T>>.reflect(): List<List<T>> =
    this.map { it.reversed() }

fun <T> List<List<T>>.rotate(quarterTurnsCCW: Int): List<List<T>> =
    this.repeatedlyApply(quarterTurnsCCW) { matrix ->
        (0 until matrix[0].indices.last + 1).reversed().map { i ->
            (0 until matrix.indices.last + 1).map { j ->
                matrix[j][i]
            }
        }
    }

data class Orientation(val flipped: Boolean, val quarterTurnsCCW: Int) {
//    override fun toString(): String {
//        val flippedString = if (flipped) "R" else "_"
//        return "O(${flippedString}$quarterTurnsCCW)"
//    }

    companion object {
        val first = Orientation(false, 0)
        fun iterator() = listOf(
            Orientation(false, 0),
            Orientation(false, 1),
            Orientation(false, 2),
            Orientation(false, 3),
            Orientation(true, 0),
            Orientation(true, 1),
            Orientation(true, 2),
            Orientation(true, 3),
        ).listIterator()
    }
}

class OrientedTileIterator(val tiles: List<Tile>) : Iterator<OrientedTile> {
    var tileIndex = 0
    var orientation = Orientation.iterator()

    override fun hasNext(): Boolean = !(tileIndex == tiles.size - 1 && !orientation.hasNext())

    override fun next(): OrientedTile {
        if (!orientation.hasNext()) {
            orientation = Orientation.iterator()
            tileIndex++
        }
        return OrientedTile(
            tiles[tileIndex],
            orientation.next()
        )
    }
}

fun <T> List<T>.dropFirstAndLast() = this.subList(1, this.size - 1)

