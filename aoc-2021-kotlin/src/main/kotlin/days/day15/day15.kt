package days.day15

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import java.util.PriorityQueue

fun main() {
    val input = Reader("day15.txt").strings()
    val exampleInput = Reader("day15-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(696)

    time(message = "Part 2", iterations = 3, warmUpIterations = 0) {
        part2(input)
    }.checkAnswer(2952)
}

fun part1(input: List<String>) = solve(RiskLevels(parse(input)))
fun part2(input: List<String>) = solve(RiskLevels(expandGrid(parse(input))))

fun solve(riskLevels: RiskLevels): Int {
    val maxX = riskLevels.values.first().lastIndex
    val maxY = riskLevels.values.lastIndex

    val start = Vector(0, 0)
    val end = Vector(maxX, maxY)

    val queue = PriorityQueue<Pair<Vector, Int>> { a, b -> a.second - b.second }
    queue.add(Pair(start, 0))
    val cameFrom = mutableMapOf<Vector, Vector?>()
    val riskSoFar = mutableMapOf<Vector, Int>()
    cameFrom[start] = null
    riskSoFar[start] = 0

    while (queue.isNotEmpty()) {
        val (current, _) = queue.remove()

        if (current == end) break

        current.neighboursInBounds(maxX, maxY)
            .forEach { next ->
                val newRisk = riskSoFar[current]!! + riskLevels.at(next)
                if (next !in riskSoFar || newRisk < riskSoFar[next]!!) {
                    riskSoFar[next] = newRisk
                    queue.add(Pair(next, newRisk))
                    cameFrom[next] = current
                }
            }
    }

    return riskSoFar[end]!!
}

fun Vector.neighboursInBounds(maxX: Int, maxY: Int) =
    neighbours
        .filterNot { it.x < 0 || it.x > maxX }
        .filterNot { it.y < 0 || it.y > maxY }

data class RiskLevels(val values: List<List<Int>>) {
    fun at(point: Vector) = values[point.y][point.x]
}

fun expandGrid(grid: List<List<Int>>): List<List<Int>> {
    val top = grid.map { one ->
        val two = one.incRow()
        val three = two.incRow()
        val four = three.incRow()
        val five = four.incRow()
        one + two + three + four + five
    }

    val one = top
    val two = one.incGrid()
    val three = two.incGrid()
    val four = three.incGrid()
    val five = four.incGrid()

    return one + two + three + four + five
}

fun List<Int>.incRow() = map { it % 9 + 1 }
fun List<List<Int>>.incGrid() = map { row -> row.incRow() }

fun parse(input: List<String>): List<List<Int>> =
    input.map { row -> row.toList().map { cell -> cell.digitToInt() } }