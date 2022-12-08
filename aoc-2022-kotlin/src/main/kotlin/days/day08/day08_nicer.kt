package days.day08

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day08.txt").strings().map { it.map(Char::digitToInt) }
    val exampleInput = Reader("day08-example.txt").strings().map { it.map(Char::digitToInt) }

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1787)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(440640)
}

private fun part1(grid: List<List<Int>>) =
    grid.points().filter { it.isVisibleFromEdge(grid) }.distinct().size

private fun part2(grid: List<List<Int>>) =
    grid.points().maxOf { it.scenicScore(grid) }

private fun Pair<Int, Int>.isVisibleFromEdge(grid: List<List<Int>>): Boolean {
    val tree = grid[second][first]
    return Direction.values().any { d -> this.pathToEdge(d, grid).all { it < tree } }
}

private fun Pair<Int, Int>.scenicScore(grid: List<List<Int>>): Int {
    val tree = grid[second][first]

    return Direction.values().fold(1) { acc, direction ->
        val path = pathToEdge(direction, grid)
        val visibleDistance = path.indexOfFirst { it >= tree }.let {
            if (it == -1) path.toList().size
            else it + 1
        }
        visibleDistance * acc
    }
}

private fun <T> Pair<Int, Int>.pathToEdge(direction: Direction, grid: List<List<T>>) =
    when (direction) {
        Direction.UP -> (second - 1 downTo 0).asSequence().map { y -> grid[y][first] }
        Direction.DOWN -> (second + 1 until grid.size).asSequence().map { y -> grid[y][first] }
        Direction.LEFT -> (first - 1 downTo 0).asSequence().map { x -> grid[second][x] }
        Direction.RIGHT -> (first + 1 until grid[second].size).asSequence().map { x -> grid[second][x] }
    }

private fun <T> List<List<T>>.points(): List<Pair<Int, Int>> =
    this.indices.flatMap { y -> this[y].indices.map { x -> Pair(x, y) } }

private enum class Direction { UP, DOWN, LEFT, RIGHT }
