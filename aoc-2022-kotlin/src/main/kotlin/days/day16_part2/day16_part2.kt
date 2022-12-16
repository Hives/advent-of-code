package days.day16_part2

import lib.Reader
import lib.checkAnswer

fun main() {
    val input = Reader("day16.txt").strings()
    val exampleInput = Reader("day16-example.txt").strings()

    part2(exampleInput).checkAnswer(1707)
    println(part2(input))
}

fun part2(input: List<String>): Int {
    val valveData = parse(input)
    val valvesWithNonZeroFlow =
        valveData.filter { it.value.first > 0 }.map { it.key }.toSet()

    val importantValves = valvesWithNonZeroFlow + START_VALVE

    val miniMap = importantValves.associateWith { from ->
        (importantValves - from).associateWith { to ->
            valveData.findShortestDistance(from, to) + 1
        }
    }

    val start = listOf("AA")
    var bestScore = 0

    val paths = mutableSetOf(listOf(start, start))

    val pressures = mutableMapOf(start to 0)
    fun evaluatePressure(path: List<String>) =
        days.day16_part1.evaluatePressure(path, TOTAL_MINUTES_PART_2, miniMap, valveData)
    fun getPressure(path: List<String>) =
        pressures[path] ?: run {
            val score = evaluatePressure(path)
            pressures[path] = score
            score
        }

    val times = mutableMapOf(start to 0)
    fun getTime(path: List<String>) =
        times[path] ?: run {
            val previousTime = times[path.subList(0, path.size - 1)]!!
            val additionalTime = miniMap[path[path.size - 2]]!![path[path.size - 1]]!!
            val newTime = previousTime + additionalTime
            times[path] = newTime
            newTime
        }

    while (paths.isNotEmpty()) {
        val nextToTry = paths.maxBy { getPressure(it[0]) + getPressure(it[1]) }
        paths.remove(nextToTry)

        val (first, second) = nextToTry
        val reachableFromFirst = findReachable(
            path = first,
            visited = first + second,
            totalTime = TOTAL_MINUTES_PART_2,
            miniMap = miniMap
        )
        val extensionsOfFirst = reachableFromFirst.map { first + it }

        val reachableFromSecond = findReachable(
            path = second,
            visited = first + second,
            totalTime = TOTAL_MINUTES_PART_2,
            miniMap = miniMap
        )
        val extensionsOfSecond = reachableFromSecond.map { second + it }

        val newPathses =
            extensionsOfFirst.map { new -> listOf(new, second) } +
                    extensionsOfSecond.map { new -> listOf(first, new) }

        newPathses.forEach {
            val pressure = getPressure(it[0]) + getPressure(it[1])
            if (pressure > bestScore) {
                bestScore = pressure
            }

            val shorterPath = it.minBy { getTime(it) }

            val remainingTime = TOTAL_MINUTES_PART_2 - getTime(shorterPath)
            val closedValves = valvesWithNonZeroFlow - it[0].toSet() - it[1].toSet()
            val potentialPressure = pressure + closedValves.sumOf { v ->
                (remainingTime - miniMap[shorterPath.last()]!![v]!!) * valveData[v]!!.first
            }
            if (potentialPressure > bestScore) paths.add(it)
        }
    }

    return bestScore
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

const val TOTAL_MINUTES_PART_2 = 26
const val START_VALVE = "AA"
