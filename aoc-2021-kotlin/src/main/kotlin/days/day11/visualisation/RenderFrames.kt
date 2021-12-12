package days.day11.visualisation

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun renderFrames(steps: List<List<List<List<String>>>>) {
    val maxFlashes = steps.maxOf { it.size }
    println("max flashes: $maxFlashes")

    val frames = steps.flatMap { flashes ->
        // we pad each step out to the length of maxFlashes by copying the last frame the required number of times
        flashes + List(maxFlashes - flashes.size) { flashes.last() }
    }

    frames.forEachIndexed { index, frame ->
        if (index % 100 == 0) println("rendering frame $index of ${frames.size}")

        val size = 960
        val gridSpace = (size - 20) / 10

        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        val g = image.graphics

        g.font = Font("Monospaced", Font.PLAIN, 100)


        frame.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell == "*") {
                    g.color = Color.MAGENTA
                } else {
                    g.color = (30 + (25 * cell.toInt())).let { Color(it, it, it) }
                }

                g.drawString(cell, 25 + (x * gridSpace), gridSpace + (y * gridSpace))
            }
        }

        g.dispose()

        ImageIO.write(image, "png", File("/home/hives/tmp/octo/frame-${index.toString().padStart(4, '0')}.png"))
    }

}

