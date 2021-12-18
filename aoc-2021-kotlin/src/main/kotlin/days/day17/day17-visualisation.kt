package days.day17

import lib.Reader
import lib.Vector
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val input = Reader("day17.txt").string()

    visualise(input)
}

fun visualise(input: String) {
    val target = parse(input)

    val successes = (-500..500).flatMap { y ->
        (0..500).map { x ->
            Vector(x, y)
        }
    }.mapNotNull { velocity ->
        launch(velocity, target).let { (trajectory, hitOrMiss) ->
            if (hitOrMiss == HitOrMiss.HIT) Pair(velocity, trajectory) else null
        }
    }

    val (maxX, minY, maxY) = successes.map { it.first }.let { velocities ->
        val maxX = velocities.maxOfOrNull { it.x }!!
        val minY = velocities.minOfOrNull { it.y }!!
        val maxY = velocities.maxOfOrNull { it.y }!!
        Triple(maxX, minY, maxY)
    }

    val scale = 3

    val image = BufferedImage(scale * maxX, scale * (maxY - minY), BufferedImage.TYPE_INT_RGB)
    val g = image.graphics
    successes.forEach { (velocity, trajectory) ->
        g.color = Color.getHSBColor(trajectory.size / 25f, 1.0f, 1.0f)

        g.fillRect(velocity.x * scale, (velocity.y - minY) * scale, scale, scale)
    }
    g.dispose()

    ImageIO.write(image, "png", File("/home/hives/tmp/aoc-2021-day-17-2.png"))

}
