package days.day19

import kotlin.math.min
import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day19/input.txt").string()
    val exampleInput = Reader("/day19/example-1.txt").string()

    part1(input).also(::println)

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

sealed class Either<A, B> {
    data class Left<A>(val value: A) : Either<A, Nothing>()
    data class Right<B>(val value: B) : Either<Nothing, B>()
}

fun part1(input: String): Int {
    val (towels, designs) = parseInput(input)
    val maxTowelLength = towels.maxOf { it.length }

    val towelMap = mutableMapOf<Any, Any>()

    towels.forEach { towel ->
        var m = towelMap
        "${towel}X".toList().forEach { char ->
            if (char !in m) {
                m[char] = mutableMapOf<Any, Any>()
            }
            m = m[char] as MutableMap<Any, Any>
        }
    }

    println(towelMap)

//    val d = designs.last()
//    println(d)
//    assessDesign(d, d, towelMap, maxTowelLength, emptyList()).also(::println)

    return designs.filter { design ->
        val result = assessDesign(design, design, towelMap, maxTowelLength, emptyList())
        println("${design}: $result")
        result
    }.size

    return -1
}

fun part2(input: String): Int {
    return -1
}

fun assessDesign(
    originalDesign: String,
    subDesign: String,
    towelMap: Map<Any, Any>,
    maxTowelLength: Int,
    previous: List<String>
): Boolean {
    println(previous)
    return if ((previous.joinToString("") == originalDesign)) {
        true
    } else {
        val towelsMatchingStart = getTowelsMatchingStart(subDesign, towelMap, maxTowelLength)
        if (towelsMatchingStart.isEmpty()) {
            false
        } else {
            towelsMatchingStart.any {
                assessDesign(
                    originalDesign = originalDesign,
                    subDesign = subDesign.substring(it.length),
                    towelMap = towelMap,
                    maxTowelLength = maxTowelLength,
                    previous = previous + it
                )
            }
        }
    }
}

fun getTowelsMatchingStart(
    design: String,
    towelMap: Map<Any, Any>,
    maxTowelLength: Int
): List<String> {
    return (1..(min(maxTowelLength, design.length))).mapNotNull {
        var m = towelMap
        val designSubstring = design.substring(0, it)
        designSubstring.toList().forEach { c ->
            if (c in m) {
                m = m[c] as Map<Any, Any>
            } else {
                return@mapNotNull null
            }
        }
        if ('X' in m) designSubstring
        else null
    }
}

fun parseInput(input: String): Pair<Set<String>, List<String>> =
    input.split("\n\n").let { (first, second) ->
        Pair(first.split(", ").toSet(), second.split("\n"))
    }
