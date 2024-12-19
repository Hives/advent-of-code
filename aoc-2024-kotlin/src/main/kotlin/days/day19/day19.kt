package days.day19

import kotlin.math.min
import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day19/input.txt").string()
    val exampleInput = Reader("/day19/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(255)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
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

    return designs.filter { design ->
        val result = assessDesign(design, design, towelMap, maxTowelLength, emptyList())
        result
    }.size
}

fun part2(input: String): Int {
    return -1
}

val resultsCache = mutableMapOf<String, Boolean>()

fun assessDesign(
    originalDesign: String,
    subDesign: String,
    towelMap: Map<Any, Any>,
    maxTowelLength: Int,
    previous: List<String>
): Boolean {
    if (subDesign in resultsCache) {
        return resultsCache[subDesign]!!
    }

    return if ((previous.joinToString("") == originalDesign)) {
        resultsCache[subDesign] = true
        true
    } else {
        val towelsMatchingStart = getTowelsMatchingStart(subDesign, towelMap, maxTowelLength)
        if (towelsMatchingStart.isEmpty()) {
            resultsCache[subDesign] = false
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
            }.also { resultsCache[subDesign] = it }
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
