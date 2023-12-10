package days.day10

import days.day10.Tile.Ground
import days.day10.Tile.Pipe
import days.day10.Tile.Start
import lib.CompassDirection
import lib.CompassDirection.E
import lib.CompassDirection.N
import lib.CompassDirection.S
import lib.CompassDirection.W
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day10/input.txt").grid()
    val exampleInput1 = Reader("/day10/example-1.txt").grid()
    val exampleInput2 = Reader("/day10/example-2.txt").grid()
    val exampleInput3 = Reader("/day10/example-3.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(6806)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(449)
}

fun part1(input: List<List<Char>>): Int =
    parse(input).getPath().size / 2

fun part2(input: List<List<Char>>): Int {
    val grid = parse(input)
    val path = grid.getPath().toSet()
    val nonPathPoints = (grid.points - path).toMutableSet()

    val patches = mutableListOf<Set<Vector>>()

    while (nonPathPoints.isNotEmpty()) {
        var front = setOf(nonPathPoints.first())
        val patch = mutableSetOf<Vector>()
        while (front.isNotEmpty()) {
            patch.addAll(front)
            nonPathPoints.removeAll(front)
            front = front.flatMap { it.neighbours }.toSet().intersect(nonPathPoints)
        }
        patches.add(patch)
    }

    return patches.filter { patch ->
        val pathToPatch = (0..patch.first().x).map { Vector(it, patch.first().y) }

        val crossingCount = pathToPatch.fold(Pair(0, null as Char?)) { acc, point ->
            val (crossCount, turnStart) = acc
            val tile = if (point in path) grid.at(point) else Ground
            if (tile is Pipe) {
                when(tile.char) {
                    '|' -> Pair(crossCount + 1, turnStart)
                    'F' -> Pair(crossCount, 'F')
                    'L' -> Pair(crossCount, 'L')
                    '7' -> if (turnStart == 'L') Pair(crossCount + 1, null) else acc
                    'J' -> if (turnStart == 'F') Pair(crossCount + 1, null) else acc
                    else -> acc
                }
            } else {
                acc
            }
        }.first

        crossingCount % 2 == 1
    }.flatten().count()
}

fun parse(grid: List<List<Char>>): Grid =
    grid.map { row ->
        row.map { tile ->
            when (tile) {
                // n.b. North = (0, 1), which is downwards 😲
                '|' -> Pipe(setOf(N, S), tile)
                '-' -> Pipe(setOf(E, W), tile)
                'L' -> Pipe(setOf(S, E), tile)
                'J' -> Pipe(setOf(S, W), tile)
                '7' -> Pipe(setOf(N, W), tile)
                'F' -> Pipe(setOf(N, E), tile)
                '.' -> Ground
                'S' -> {
                    Start
                }

                else -> throw Error("Bad tile: $tile")
            }
        }
    }.let(::Grid)

data class Grid(val tiles: List<List<Tile>>) {
    val points
        get(): List<Vector> {
            val ys = tiles.indices
            val xs = tiles[0].indices
            return ys.flatMap { y -> xs.map { x -> Vector(x = x, y = y) } }
        }

    fun getPath(): List<Vector> {
        val startPoint = points.first { at(it) == Start }

        val startDirections = CompassDirection.values().filter { direction ->
            val neighbour = at(startPoint + direction.vector)
            neighbour is Pipe && neighbour.connections.contains(direction.opposite())
        }

        val path = mutableListOf(startPoint)

        var direction = startDirections.first()

        while (true) {
            val nextPoint = path.last() + direction.vector
            val nextTile = at(nextPoint)
            path.add(nextPoint)
            if (nextTile == Start) break
            if (nextTile !is Pipe) throw Error("Landed on ground?!")
            val nextDirection = nextTile.connections.first { it != direction.opposite() }
            direction = nextDirection
        }

        return path
    }

    fun at(point: Vector): Tile? =
        if (point.x < 0 || point.y < 0 || point.x >= tiles[0].size || point.y >= tiles.size) null
        else tiles[point.y][point.x]
}

sealed class Tile {
    object Start : Tile()
    object Ground : Tile()
    data class Pipe(val connections: Set<CompassDirection>, val char: Char) : Tile()
}
