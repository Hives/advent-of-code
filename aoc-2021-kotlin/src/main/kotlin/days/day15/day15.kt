package days.day15

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day15.txt").strings()
    val exampleInput = Reader("day15-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(696)

    time(message = "Part 2", iterations = 10, warmUpIterations = 10) {
        part2(input)
    }.checkAnswer(2952)
}

fun part1(input: List<String>) = solve(RiskLevels(parse(input)))

fun part2(input: List<String>): Int {
    val topLeft = parse(input)

    val top = topLeft.map { row ->
        val one = row
        val two = incRow(one)
        val three = incRow(two)
        val four = incRow(three)
        val five = incRow(four)
        one + two + three + four + five
    }

    val one = top
    val two = incGrid(one)
    val three = incGrid(two)
    val four = incGrid(three)
    val five = incGrid(four)

    val bigGrid = one + two + three + four + five

    val riskLevels = RiskLevels(bigGrid)
    return solve(riskLevels)
}

fun solve(riskLevels: RiskLevels): Int {
    val start = Vector(0, 0)
    val end = Vector(riskLevels.maxX, riskLevels.maxY)

    val queue = PriorityQueue(mutableMapOf<Vector, Int>())
    queue.put(start, 0)
    val cameFrom = mutableMapOf<Vector, Vector?>()
    val riskSoFar = mutableMapOf<Vector, Int>()
    cameFrom[start] = null
    riskSoFar[start] = 0

    while (queue.isNotEmpty()) {
        val current = queue.get()!!

        if (current == end) break

        current.neighbours
            .filterNot { it.x < 0 || it.x > riskLevels.maxX }
            .filterNot { it.y < 0 || it.y > riskLevels.maxY }
            .forEach { next ->
                val newRisk = riskSoFar[current]!! + riskLevels.at(next)
                if (next !in riskSoFar.keys || newRisk < riskSoFar[next]!!) {
                    riskSoFar[next] = newRisk
                    queue.put(next, newRisk)
                    cameFrom[next] = current
                }
            }
    }

    return riskSoFar[end]!!
}

data class PriorityQueue<T, P : Comparable<P>>(private var queue: MutableMap<T, P>) {
    fun put(value: T, priority: P) {
        queue[value] = priority
    }

    fun get(): T? {
        val item = queue.toList().minByOrNull { it.second }
        item?.also { queue.remove(it.first) }
        return item?.first
    }

    fun isNotEmpty() = queue.isNotEmpty()
}

data class RiskLevels(private val riskLevels: List<List<Int>>) {
    fun at(point: Vector) = riskLevels[point.y][point.x]

    val maxX = riskLevels.last().lastIndex
    val maxY = riskLevels.lastIndex
}

fun incRow(row: List<Int>) = row.map {
    val foo = it + 1
    if (foo > 9) 1 else foo
}
fun incGrid(grid: List<List<Int>>) = grid.map { row -> incRow(row) }

fun parse(input: List<String>): List<List<Int>> =
    input.map { row -> row.toList().map { cell -> cell.digitToInt() } }