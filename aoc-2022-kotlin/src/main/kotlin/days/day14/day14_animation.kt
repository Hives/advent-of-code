package days.day14

import lib.Reader
import lib.Vector
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val input = Reader("day14.txt").strings()
    val exampleInput = Reader("day14-example.txt").strings()

    animatePart2(input)
}

fun animatePart2(input: List<String>) {
    val grids = mutableListOf<List<List<Char>>>()
    val grid = parse(input, addFloor = true)
    grids.add(grid.grid)

    do {
        val final = grid.produce()
        grids.add(grid.grid)
    } while (final != Vector(500, 0))

    repeat(500) {
        grids.add(grids.last())
    }

    grids.mapIndexedNotNull { index, it ->
        if (index % 20 == 0) it else null
    }.forEachIndexed { i, grid ->
        renderFrame(grid, i)
    }
}

fun renderFrame(grid: List<List<Char>>, index: Int) {
    val multiplier = 1;

    val image = BufferedImage(grid[0].size * multiplier, grid.size * multiplier, BufferedImage.TYPE_INT_RGB)
    val g = image.graphics

    grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            g.color = when (cell) {
                SAND -> Color.MAGENTA
                ROCK -> Color.GREEN
                else -> Color.BLACK
            }
            g.fillRect(x * multiplier, y * multiplier, multiplier, multiplier)
        }
    }

    g.dispose()

    ImageIO.write(
        image,
        "png",
        File("/home/hives/tmp/regolith-reservoir/frame-${index.toString().padStart(5, '0')}.png")
    )
}
