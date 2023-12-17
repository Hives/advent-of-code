package days.day17

import lib.IntGrid
import lib.Reader
import lib.Vector
import lib.repeatedlyApply
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.util.PriorityQueue
import javax.imageio.ImageIO

fun main() {
    // part 2 takes 747,809 iterations
    // at 60fps and targeting ~1 minute, that comes out at about 200 iterations per frame.

    val grid = Reader("/day17/input.txt").intGrid()

    var iterationCount = 0;
    var frameCount = 0;

    fun aStar(grid: IntGrid, bits: Bits): FinalState {
        val start = Node(Vector(0, 0), 0, Direction.Right)
        val goal = Vector(grid.last().size - 1, grid.size - 1)

        fun heuristic(node: Node) = (goal - node.location).manhattanDistance

        val cameFrom = mutableMapOf<Node, Node>()
        val gScore = mutableMapOf(start to 0)
        val fScore = mutableMapOf(start to heuristic(start))

        val openSet = PriorityQueue { a: Node, b: Node ->
            fScore.getOrDefault(a, Int.MAX_VALUE).compareTo(fScore.getOrDefault(b, Int.MAX_VALUE))
        }
        openSet.add(start)

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()

            iterationCount++;
            if (iterationCount % 1_000 == 0) {
                println(iterationCount)
                renderFrame(grid, current, openSet.toSet(), gScore, cameFrom, frameCount++)
            }

            if (bits.isGoal(current, grid)) {
                return FinalState(current, cameFrom, gScore, openSet.toSet())
            }

            val possibleNext = bits.getViableNextNodes(current, grid)
            possibleNext.forEach { next ->
                val tentativeGScore = gScore[current]!! + grid.at(next.location)!!
                if (tentativeGScore < (gScore.getOrDefault(next, Int.MAX_VALUE))) {
                    cameFrom[next] = current
                    gScore[next] = tentativeGScore
                    fScore[next] = tentativeGScore + heuristic(next)
                    if (next !in openSet) openSet.add(next)
                }
            }
        }

        throw Error("Oh no")
    }

    val finalState = aStar(grid, UltraCrucibleBits)

    val path = reconstructPath(emptyList(), finalState.current, finalState.cameFrom)

    path.indices.forEach { index ->
        if (index % 10 == 0) {
            val subPath = path.subList(0, index + 1)
            renderPath(subPath, grid, finalState.openSet, finalState.gScore, index)
        }
    }
    repeat(50) { n ->
        renderPath(path, grid, finalState.openSet, finalState.gScore, 500 + n)
    }
}

fun renderFrame(
    grid: IntGrid,
    current: Node,
    openSet: Set<Node>,
    gScore: Map<Node, Int>,
    cameFrom: Map<Node, Node>,
    n: Int
) {
    val (image, g) = createBackground(grid)
    val path = reconstructPath(emptyList(), current, cameFrom)
    g.drawFrame(grid, openSet, gScore, path)
    g.dispose()
    ImageIO.write(
        image,
        "png",
        File("/home/hives/tmp/lava/lava${n.toString().padStart(5, '0')}.png")
    )
}

fun renderPath(
    path: List<Vector>,
    grid: IntGrid,
    openSet: Set<Node>,
    gScore: Map<Node, Int>,
    n: Int
) {
    val (image, g) = createBackground(grid)
    g.drawFrame(grid, openSet, gScore, path)
    g.color = Color.MAGENTA
    path.windowed(2).forEach { (a, b) ->
        g.drawRect(a.x * 6, a.y * 6, 5, 5)
    }
    g.dispose()
    ImageIO.write(
        image,
        "png",
        File("/home/hives/tmp/lava/lava1${n.toString().padStart(4, '0')}.png")
    )
}

fun Graphics2D.drawFrame(
    grid: IntGrid,
    openSet: Set<Node>,
    gScore: Map<Node, Int>,
    path: List<Vector>
) {
    gScore.forEach { (node, value) ->
        val heat = grid.at(node.location)!!
        val red = (Color.RED.repeatedlyApply(10 - heat - 1) { it.darker() }.red / 255.0).toFloat()
        val blue = (value / 1400.0).toFloat()
        color = Color(red, 0.0.toFloat(), blue)
        fillRect((node.location.x * 6), (node.location.y * 6), 5, 5)
//        g.blob(node.location.x * 6, node.location.y * 6)
    }
    openSet.forEach { node ->
        val point = node.location
        color = Color.MAGENTA
        drawRect(point.x * 6, point.y * 6, 4, 4)
    }
}

fun createBackground(grid: IntGrid): Pair<BufferedImage, Graphics2D> {
    val w = (6 * grid[0].size) - 1
    val h = (6 * grid.size) - 1
    val image = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    val g = image.createGraphics()

    grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, heat ->
            g.color = Color.RED.repeatedlyApply(10 - heat - 1) { it.darker() }
            g.fillRect(x * 6, y * 6, 5, 5)
//            g.blob(x * 6, y * 6)
        }
    }

    return Pair(image, g)
}

tailrec fun reconstructPath(path: List<Vector>, current: Node, cameFrom: Map<Node, Node>): List<Vector> {
    if (current !in cameFrom) return path + Vector(0, 0)
    else {
        val previous = cameFrom[current]!!
        return reconstructPath(path + current.location, previous, cameFrom)
    }
}

fun Graphics2D.blob(x: Int, y: Int) {
    fillRect(x + 1, y, 3, 5)
    fillRect(x, y + 1, 5, 3)
}

data class FinalState(
    val current: Node,
    val cameFrom: Map<Node, Node>,
    val gScore: Map<Node, Int>,
    val openSet: Set<Node>
)
