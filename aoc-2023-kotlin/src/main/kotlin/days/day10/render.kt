package days.day10

import lib.Reader
import lib.Vector
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val input = Reader("/day10/input.txt").grid()
    val grid = parse(input)
    val path = getPath(grid)
    val (internalPoints, externalPoints) = getInternalAndExternalPoints(grid, path.toSet())

    val image = BufferedImage((6 * grid.tiles[0].size) - 1, (6 * grid.tiles.size) - 1, BufferedImage.TYPE_INT_RGB)
    val g = image.graphics

    path.forEach { point ->
        when (val tile = grid.at(point)) {
            Tile.Start -> {
                g.color = Color.GREEN
                g.fillRect(point.x * 6, point.y * 6, 5, 1)
                g.fillRect(point.x * 6, point.y * 6 + 2, 5, 1)
                g.fillRect(point.x * 6, point.y * 6 + 4, 5, 1)
                g.fillRect(point.x * 6, point.y * 6, 1, 3)
                g.fillRect((point.x * 6) + 4, (point.y * 6) + 2, 1, 3)
            }
            is Tile.Pipe -> {
                g.color = Color.GREEN
                when (tile.char) {
                    '|' -> g.fillRect((point.x * 6) + 2, point.y * 6, 1, 5)
                    '-' -> g.fillRect(point.x * 6, (point.y * 6) + 2, 5, 1)
                    'F' -> {
                        g.fillRect((point.x * 6) + 2, (point.y * 6) + 2, 3, 1)
                        g.fillRect((point.x * 6) + 2, (point.y * 6) + 2, 1, 3)
                    }
                    '7' -> {
                        g.fillRect((point.x * 6), (point.y * 6) + 2, 3, 1)
                        g.fillRect((point.x * 6) + 2, (point.y * 6) + 2, 1, 3)
                    }
                    'J' -> {
                        g.fillRect((point.x * 6), (point.y * 6) + 2, 3, 1)
                        g.fillRect((point.x * 6) + 2, (point.y * 6), 1, 3)
                    }
                    'L' -> {
                        g.fillRect((point.x * 6) + 2, (point.y * 6) + 2, 3, 1)
                        g.fillRect((point.x * 6) + 2, (point.y * 6), 1, 3)
                    }
                }
            }
            else -> {}
        }
    }

    internalPoints.forEach { point ->
        g.color = Color.MAGENTA
        g.fillRect(point.x * 6, point.y * 6, 5, 5)
    }

    externalPoints.forEach { point ->
        g.color = Color.CYAN
        g.fillRect(point.x * 6, point.y * 6, 5, 5)
    }

    g.dispose()

    ImageIO.write(
        image,
        "png",
        File("/home/hives/tmp/pipe-maze/pic.png")
    )
}

fun getInternalAndExternalPoints(grid: Grid, path: Set<Vector>): Pair<Set<Vector>, Set<Vector>> {
    val nonPathPoints = (grid.points - path).toSet()

    val nonPathPointsMutable = nonPathPoints.toMutableSet()

    val patches = mutableListOf<Set<Vector>>()

    while (nonPathPointsMutable.isNotEmpty()) {
        var front = setOf(nonPathPointsMutable.first())
        val patch = mutableSetOf<Vector>()
        while (front.isNotEmpty()) {
            patch.addAll(front)
            nonPathPointsMutable.removeAll(front)
            front = front.flatMap { it.neighbours }.toSet().intersect(nonPathPointsMutable)
        }
        patches.add(patch)
    }

    val internalPoints = patches.filter { patch ->
        val pathToPatch = (0..patch.first().x).map { Vector(it, patch.first().y) }

        val crossingCount = pathToPatch.fold(Pair(0, null as Char?)) { acc, point ->
            val (crossCount, turnStart) = acc
            val tile = if (point in path) grid.at(point) else Tile.Ground
            if (tile is Tile.Pipe) {
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
    }.flatten().toSet()

    val externalPoints = nonPathPoints - internalPoints

    return Pair(internalPoints, externalPoints)
}
