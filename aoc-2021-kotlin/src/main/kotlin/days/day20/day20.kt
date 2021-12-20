package days.day20

import lib.Reader
import lib.Vector
import lib.checkAnswer

fun main() {
    val input = Reader("day20.txt").string()
    val exampleInput = Reader("day20-example.txt").string()

    part1(input).checkAnswer(5301)
    part2(input).checkAnswer(19492)
}

fun part1(input: String): Int {
    val (imageEnhancementAlgorithm, image) = parse(input)

    return multoEnhanco(image, imageEnhancementAlgorithm, 2).size
}

fun part2(input: String): Int {
    val (imageEnhancementAlgorithm, image) = parse(input)

    return multoEnhanco(image, imageEnhancementAlgorithm, 50).size
}

fun multoEnhanco(imagePoints: Set<Vector>, imageEnhancementAlgorithm: String, n: Int): Set<Vector> =
    if (n == 0) imagePoints
    else {

        val once = enhanceOnce(imagePoints, imageEnhancementAlgorithm)
        val twice = enhanceOnce(once, imageEnhancementAlgorithm)

        val minX = twice.minOfOrNull { it.x }!! + 11
        val maxX = twice.maxOfOrNull { it.x }!! - 11

        val minY = twice.minOfOrNull { it.y }!! + 11
        val maxY = twice.maxOfOrNull { it.y }!! - 11

        val withBorderRemoved = twice.filter { it.x in minX..maxX && it.y in minY..maxY }.toSet()

        multoEnhanco(withBorderRemoved, imageEnhancementAlgorithm, n - 2)
    }

fun enhanceOnce(imagePoints: Set<Vector>, imageEnhancementAlgorithm: String): Set<Vector> {
    val minX = imagePoints.minOfOrNull { it.x }!! - 10
    val maxX = imagePoints.maxOfOrNull { it.x }!! + 10
    val xRange = minX..maxX

    val minY = imagePoints.minOfOrNull { it.y }!! - 10
    val maxY = imagePoints.maxOfOrNull { it.y }!! + 10
    val yRange = minY..maxY

    val bigSquare = yRange.flatMap { y -> xRange.map { x -> Vector(x, y) } }.toSet()

    return bigSquare.filter { point ->
        enhancePoint(point, imagePoints, imageEnhancementAlgorithm) == PixelStatus.ON
    }.toSet()
}

fun enhancePoint(
    point: Vector,
    imagePoints: Set<Vector>,
    imageEnhancementAlgorithm: String,
): PixelStatus {
    val index = (point.surrounding + point).sorted().map { point ->
        if (point in imagePoints) '1' else '0'
    }.joinToString("").toInt(2)

    return if (imageEnhancementAlgorithm[index] == '#') PixelStatus.ON else PixelStatus.OFF
}

enum class PixelStatus { ON, OFF }
enum class OddOrEven { ODD, EVEN }

fun printy(points: Set<Vector>) {
    println()
    println()

    val minX = points.minOfOrNull { it.x }!!
    val maxX = points.maxOfOrNull { it.x }!!
    val minY = points.minOfOrNull { it.y }!!
    val maxY = points.maxOfOrNull { it.y }!!

    (minY..maxY).toList().forEach { y ->
        (minX..maxX).toList().map { x ->
            if (Vector(x, y) in points) '#' else '.'
        }.joinToString("")
            .also { println(it) }
    }
}

fun parse(input: String): Pair<String, Set<Vector>> {
    val (imageEnhancementAlgorithm, image) = input.split("\n\n")

    val points = image.lines().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            if (c == '#') Vector(x, y)
            else null
        }
    }.toSet()

    return Pair(imageEnhancementAlgorithm, points)
}