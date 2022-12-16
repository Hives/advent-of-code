package days.day16_part1

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day16.txt").strings()
    val exampleInput = Reader("day16-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(1828)
}

fun part1(input: List<String>): Int {
    val valveData = parse(input)
    val valvesWithNonZeroFlow =
        valveData.filter { it.value.first > 0 }.map { it.key }.toSet()

    val importantValves = valvesWithNonZeroFlow + START_VALVE

    val miniMap = importantValves.associateWith { from ->
        (importantValves - from).associateWith { to ->
            valveData.findShortestDistance(from, to) + 1
        }
    }

    val paths = extend(listOf("AA"), TOTAL_MINUTES_PART_1, miniMap)

    return paths.associateWith { evaluatePressure(it, TOTAL_MINUTES_PART_1, miniMap, valveData) }
        .toList()
        .maxBy { it.second }
        .second
}

fun extend(
    path: List<String>,
    totalTime: Int,
    miniMap: Map<String, Map<String, Int>>
): List<List<String>> {
    val nextValves = findReachable(path, path, totalTime, miniMap)
    return if (nextValves.isEmpty()) listOf(path)
    else nextValves.flatMap { extend(path + it, totalTime, miniMap) }
}

fun findReachable(
    path: List<String>,
    visited: List<String>,
    totalTime: Int,
    miniMap: Map<String, Map<String, Int>>
): List<String> {
    val timeRemaining = totalTime - path.time(miniMap)
    val closedValves = miniMap.keys - visited.toSet()
    val openableValves = closedValves.filter { v ->
        miniMap[path.last()]!![v]!! <= timeRemaining
    }
    return openableValves
}

fun List<String>.time(miniMap: Map<String, Map<String, Int>>) =
    this.windowed(2, 1).fold(0) { acc, (from, to) ->
        acc + miniMap[from]!![to]!! + 1
    }

fun evaluatePressure(
    path: List<String>,
    totalTime: Int,
    miniMap: Map<String, Map<String, Int>>,
    valveData: ValveData
): Int {
    val (_, pressure) = path.windowed(2, 1).fold(Pair(totalTime, 0)) { acc, chunk ->
        val (timeRemaining, pressureReleased) = acc
        val (first, second) = chunk

        val timeToReachAndOpenValve = miniMap[first]!![second]!!
        val newTimeRemaining = timeRemaining - timeToReachAndOpenValve
        val newPressureReleased = valveData[second]!!.first * newTimeRemaining

        Pair(newTimeRemaining, pressureReleased + newPressureReleased)
    }

    return pressure
}

fun ValveData.findShortestDistance(from: String, to: String): Int {
    val valves = map { it.key }
    val distances = valves.associateWith { Int.MAX_VALUE }.toMutableMap()
    distances[from] = 0
    val queue = valves.toMutableList()
    while (true) {
        val next = queue.minBy { distances[it]!! }
        if (next == to) return distances[to]!!
        else {
            queue.remove(next)
            this[next]!!.second.forEach { neighbour ->
                val distance = distances[next]!! + 1
                if (distance < distances[neighbour]!!) distances[neighbour] = distance
            }
        }
    }
}

fun parse(input: List<String>): ValveData {
    val r = Regex("""Valve (.+) has flow rate=(\d+); tunnels? leads? to valves? (.+)""")
    return input.associate {
        r.find(it)!!.destructured.let { (id, rate, leadsTo) ->
            id to Pair(rate.toInt(), leadsTo.split(", "))
        }
    }
}

typealias ValveData = Map<String, Pair<Int, List<String>>>

const val TOTAL_MINUTES_PART_1 = 30
const val START_VALVE = "AA"
