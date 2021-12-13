package days.day13

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day13.txt").string()
    val exampleInput = Reader("day13-example.txt").string()

    time(message = "Part 1", iterations = 1_000, warmUpIterations = 30) {
        part1(input)
    }.checkAnswer(689)

    time(message = "Part 2", iterations = 1_000, warmUpIterations = 30) {
        part2(input).let { "\n" + it }
    }.checkAnswer("\n" + part2Solution)
}

val part2Solution =
    """###..#....###...##....##..##..#....#..#
      |#..#.#....#..#.#..#....#.#..#.#....#..#
      |#..#.#....###..#.......#.#....#....#..#
      |###..#....#..#.#.......#.#.##.#....#..#
      |#.#..#....#..#.#..#.#..#.#..#.#....#..#
      |#..#.####.###...##...##...###.####..##.""".trimMargin()


fun part1(input: String): Int {
    val (points, folds) = parse(input)
    val points2 = points.foldPaper(folds[0]).distinct()

    return points2.size
}

fun part2(input: String): String {
    val (points, folds) = parse(input)
    val final = folds.fold(points) { current, fold -> current.foldPaper(fold) }
    return format(final)
}

fun List<Vector>.foldPaper(fold: Pair<String, Int>): List<Vector> {
    val (axis, n) = fold

    return if (axis == "x") {
        map {
            if (it.x > n) Vector(n - (it.x - n), it.y)
            else it
        }
    } else {
        map {
            if (it.y > n) Vector(it.x, n - (it.y - n))
            else it
        }
    }
}

fun format(points: List<Vector>): String {
    val maxX = points.maxOf { it.x }
    val maxY = points.maxOf { it.y }
    val output = MutableList(maxY + 1) { MutableList(maxX + 1) { '.' } }
    points.forEach { output[it.y][it.x] = '#' }
    return output.joinToString("\n") { it.joinToString("") }
}

fun parse(input: String): Pair<List<Vector>, List<Pair<String, Int>>> {
    input.split("\n\n").let { (pointsInput, foldsInput) ->
        val points = pointsInput.lines().map {
            it.split(",")
                .let { (x, y) -> Vector(x.toInt(), y.toInt()) }
        }
        val folds = foldsInput.lines().map {
            it.substringAfterLast(" ").split("=")
                .let { (x, y) -> Pair(x, y.toInt()) }
        }
        return Pair(points, folds)
    }
}