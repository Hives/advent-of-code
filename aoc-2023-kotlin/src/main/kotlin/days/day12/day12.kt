package days.day12

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun main() {
    val input = Reader("/day12/input.txt").strings()
    val exampleInput = Reader("/day12/example-1.txt").strings()

//    time(message = "Part 1", warmUpIterations = 0, iterations = 1) {
//        part1(input)
//    }.checkAnswer(8075)

    part2(input)

    exitProcess(0)


    time(message = "Part 2") {
        part2(exampleInput)
    }.checkAnswer(0)
}

fun part1(input: List<String>) =
    input.map(::parse).sumOf { (condition, groups) ->
        getArrangements(condition.length, groups)
            ?.filter { matchesCondition(it, condition) }
            ?.size
            ?: throw Error("Something went wrong here")
    }

@OptIn(ExperimentalTime::class)
fun part2(input: List<String>): Int {
    input.map(::parse2).mapIndexed { index, line ->
        println("---- $index ----")
        println(line)
        val (arrangements, timeTaken) = measureTimedValue {
            getArrangements2(line.first, line.second)
        }
        arrangements.count().toLong()
            .also {
                println(it)
                println("time taken: $timeTaken")
            }
    }.sum().also { println(it) }

    return -1
}

fun getArrangements(length: Int, groups: List<Int>): List<String>? {
    return if (groups.isEmpty()) {
        listOf(createString(length, '.'))
    } else {
        val firstGroup = groups[0]
        val remainingGroups = groups.drop(1)
        val minTailLength = remainingGroups.let { it.sum() + it.size - 1 }
        if (firstGroup + minTailLength + 1 > length) null
        else {
            val intRange = 0 until (length - minTailLength - firstGroup)
            intRange.mapNotNull { startPadding ->
                val head = createString(startPadding, '.') + createString(firstGroup, '#')
                if (remainingGroups.isNotEmpty()) {
                    val tails = getArrangements(length - head.length - 1, remainingGroups)
                    tails?.map { "$head.$it" }
                } else listOf(head + createString(length - head.length, '.'))
            }.flatten()
        }
    }
}

fun getArrangements2(condition: String, allGroups: List<Int>): List<String> {

    fun go(head: String, groups: List<Int>): List<String> {
//        println("---------------")
//        println("head: $head")
//        println("head.length: ${head.length}")
//        println("condition.length: ${condition.length}")
//        println("groups: $groups")
        if (head.length == condition.length) return listOf(head)

        val nextHeads: List<String> =
            when {
                groups.isEmpty() -> {
//                    println("condition.length: ${condition.length}")
//                    println("head.length: ${head.length}")
                    listOf(createString(condition.length - head.length, '.'))
                }

                else -> {
                    val firstGroup = groups[0]
//                    println("firstGroup: $firstGroup")
                    val remainingGroups = groups.drop(1)
//                    println("remainingGroups: $remainingGroups")
                    val minTailLength = remainingGroups.let { it.sum() + it.size }
//                    println("minTailLength: $minTailLength")
                    val maxNextHeadLength = if (head.isEmpty()) {
                        condition.length - minTailLength
                    } else {
                        condition.length - (head.length + 1) - minTailLength
                    }
//                    println("maxNextHeadLength: $maxNextHeadLength")
                    val nextHeadRange = 0..(maxNextHeadLength - firstGroup)
//                    println("nextHeadRange: $nextHeadRange")
                    nextHeadRange.map { startPadding ->
                        createString(startPadding, '.') + createString(firstGroup, '#')
                    }
                }
            }

//        println("nextHeads: $nextHeads")

        val nextHeadsJoined = nextHeads.map {
            if (head.isEmpty()) it
            else "$head.$it"
        }

//        println("nextHeadsJoined: $nextHeadsJoined")

        val nextHeadsJoinedFiltered = nextHeadsJoined.filter { matchesCondition(it, condition) }

//        println("nextHeadsJoinedFiltered: $nextHeadsJoinedFiltered")

        return if (groups.isEmpty()) nextHeadsJoinedFiltered
        else {
            nextHeadsJoinedFiltered.flatMap { go(it, groups.drop(1)) }
        }
    }

    return go("", allGroups)
}

fun matchesCondition(input: String, match: String) =
    input.zip(match).all { (first, second) ->
        when {
            second == '?' -> true
            else -> first == second
        }
    }

fun parse(input: String): Pair<String, List<Int>> =
    input.split(' ').let { (first, second) ->
        val ns = second.split(',').map(String::toInt)
        return Pair(first, ns)
    }

fun parse2(input: String): Pair<String, List<Int>> =
    parse(input).let { (condition, groups) ->
        Pair(
            List(5) { condition }.joinToString("?"),
            List(5) { groups }.flatten()
        )
    }

fun createString(length: Int, char: Char) =
    List(length) { char }.joinToString("")
