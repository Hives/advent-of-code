package days.day08

import lib.Reader
import lib.checkAnswer
import lib.lcm
import lib.time

fun main() {
    val input = Reader("/day08/input.txt").string()
    val exampleInput = Reader("/day08/example-1.txt").string()

    time(message = "Part 1", iterations = 20) {
        part1(input)
    }.checkAnswer(16409)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(11795205644011L)
}

fun part1(input: String): Int {
    val (instructions, nodeMap) = parse(input)

    tailrec fun go(path: List<String>, i: Int): List<String> {
        val current = path.last()
        return if (current == "ZZZ") path
        else {
            val turns = nodeMap[current]!!
            val nextNode = if (instructions[i] == 'L') turns.first else turns.second
            val nextI = (i + 1) % instructions.length
            go(path + nextNode, nextI)
        }
    }

    return go(listOf("AAA"), 0).size - 1
}

fun part2(input: String): Long {
    val (instructions, nodeMap) = parse(input)
    val startingPoints = nodeMap.keys.filter { it.last() == 'A' }

    tailrec fun getFullPath(
        path: List<PathNode>,
    ): Pair<List<PathNode>, Int> {
        val current = path.last().first
        val i = path.last().second
        val turns = nodeMap[current]!!

        val nextNode = if (instructions[i] == 'L') turns.first else turns.second
        val nextI = (i + 1) % instructions.length
        val next = Pair(nextNode, nextI)

        return if (path.contains(next)) Pair(path, path.indexOf(next))
        else {
            getFullPath(path + next)
        }
    }

    return startingPoints.map { startingPoint ->
        getFullPath(listOf(Pair(startingPoint, 0)))
    }.map {
        val loopIndex = it.second
        val loopLength = it.first.size - loopIndex
        loopLength.toLong()
    }.let {
        // this wouldn't work in general, but works here because of
        // some specifics of the data
        // e.g. each looping path only touches one node ending in 'Z'
        // also the node ending in 'Z' happens to be offset from the
        // end of the loop by the same distance as the start of the loop
        // is offset from the overall start of the path
        // 🤷
        lcm(it)
    }
}

fun parse(input: String): Pair<String, Map<String, Pair<String, String>>> {
    val (turns, nodes) = input.split("\n\n")

    val nodeMap = nodes.lines().associate { line ->
        val match = Regex("""(.*) = \((.*), (.*)\)""").find(line)!!
        val (node, left, right) = match.destructured
        node to Pair(left, right)
    }

    return Pair(turns, nodeMap)
}

typealias PathNode = Pair<String, Int>
