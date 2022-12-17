package days.day16_part2

import lib.Reader
import kotlin.math.max

fun main() {
    val input = Reader("day16.txt").strings()
    val exampleInput = Reader("day16-example.txt").strings()

    // very slow - i left this running overnight and it still hadn't finished,
    // although the last number it spat out was the right one
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

    val pathPairQueue = mutableSetOf(Pair(start, start))

    val pressures = mutableMapOf(start to 0)
    fun getPressure(path: List<String>) =
        pressures[path] ?: run {
            val previousPressure = pressures[path.subList(0, path.size - 1)]!!
            val remainingTime = TOTAL_MINUTES_PART_2 - path.time(miniMap)
            val additionalPressure = valveData[path.last()]!!.first * remainingTime
            val pressure = previousPressure + additionalPressure
            pressures[path] = pressure
            pressure
        }

    fun getPressure(pathPair: Pair<List<String>, List<String>>) =
        getPressure(pathPair.first) + getPressure(pathPair.second)

    val times = mutableMapOf(start to 0)
    fun getTime(path: List<String>) =
        times[path] ?: run {
            val previousTime = times[path.subList(0, path.size - 1)]!!
            val additionalTime = miniMap[path[path.size - 2]]!![path[path.size - 1]]!!
            val newTime = previousTime + additionalTime
            times[path] = newTime
            newTime
        }

    fun potentialPressure(pathPair: Pair<List<String>, List<String>>): Int {
        val pressure = getPressure(pathPair)
        val closedValves = valvesWithNonZeroFlow - pathPair.first.toSet() - pathPair.second.toSet()
        val shorterPath = listOf(pathPair.first, pathPair.second).minBy(::getTime)
        val remainingTime = TOTAL_MINUTES_PART_2 - getTime(shorterPath)
        val potentialPressure = pressure + closedValves.sumOf { v ->
            remainingTime * valveData[v]!!.first
        }
        return potentialPressure
    }

    while (pathPairQueue.isNotEmpty()) {
//        println(pathPairQueue.size)
        val nextToTry = pathPairQueue.maxBy(::getPressure)
        pathPairQueue.remove(nextToTry)

        val unopened = miniMap.keys - nextToTry.first.toSet() - nextToTry.second.toSet()
        val newPairPaths = unopened.flatMap {
            listOf(Pair(nextToTry.first + it, nextToTry.second), Pair(nextToTry.first, nextToTry.second + it))
        }.filter {
            max(getTime(it.first), getTime(it.second)) < TOTAL_MINUTES_PART_2
        }

        newPairPaths.forEach { pathPair ->
            val pressure = getPressure(pathPair.first) + getPressure(pathPair.second)

            if (pressure > bestScore) {
                bestScore = pressure
                println("new best score: $bestScore for $pathPair")
                pathPairQueue.removeIf { potentialPressure(it) < bestScore }
            }

            if (potentialPressure(pathPair) > bestScore) pathPairQueue.add(pathPair)
        }

//        val extended = unopened.asSequence()
//            .flatMap {
//                listOf(Pair(nextToTry.first + it, nextToTry.second), Pair(nextToTry.first, nextToTry.second + it))
//            }
//            .filter { it !in visitedPathPairs }
//            .maxByOrNull(::getPressure)
////        println("extended: $extended")
//
//        if (extended == null) pathPairQueue.remove(nextToTry)
//        else {
//            visitedPathPairs.add(extended)
//            val pressure = getPressure(extended)
//            if (pressure > bestScore) {
//                bestScore = pressure
//                println("new best score: $bestScore for $extended")
//                pathPairQueue.removeIf { potentialPressure(it) < bestScore }
//            }
//            val potentialPressure = potentialPressure(extended)
//            if (potentialPressure > bestScore) {
////                println("adding $extended to queue")
//                pathPairQueue.add(extended)
//            }
//        }

//
//        val (first, second) = nextToTry
//        val reachableFromFirst = findReachable(
//            path = first,
//            visited = first + second,
//            totalTime = TOTAL_MINUTES_PART_2,
//            miniMap = miniMap
//        )
//        val extensionsOfFirst = reachableFromFirst.map { first + it }
//
//        val reachableFromSecond = findReachable(
//            path = second,
//            visited = first + second,
//            totalTime = TOTAL_MINUTES_PART_2,
//            miniMap = miniMap
//        )
//        val extensionsOfSecond = reachableFromSecond.map { second + it }
//
//        val newPathPairs =
//            extensionsOfFirst.map { new -> Pair(new, second) } +
//                    extensionsOfSecond.map { new -> Pair(first, new) }
//
//        newPathPairs.forEach { pathPair ->
//            val pressure = getPressure(pathPair.first) + getPressure(pathPair.second)
//
//            if (pressure > bestScore) {
//                bestScore = pressure
//                println("new best score: $bestScore")
//                pathPairQueue = pathPairQueue.filter { potentialPressure(it) > bestScore }.toMutableSet()
//            }
//
//            if (potentialPressure(pathPair) > bestScore) pathPairQueue.add(pathPair)
//        }
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
        acc + miniMap[from]!![to]!!
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
