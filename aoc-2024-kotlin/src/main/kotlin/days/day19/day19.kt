package days.day19

import kotlin.math.min
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day19/input.txt").string()
    val exampleInput = Reader("/day19/example-1.txt").string()

    time(message = "Part 1", iterations = 500, warmUp = 5000) {
        part1(input)
    }.checkAnswer(255)

    time(message = "Part 2", iterations = 500, warmUp = 5000) {
        part2(input)
    }.checkAnswer(621820080273474)
}

fun part1(input: String): Int {
    val (towels, designs) = parseInput(input)
    val maxTowelLength = towels.maxOf { it.length }

    val towelMap = makeTowelMap(towels)

    return designs.filter { design ->
        val result = isDesignPossible(design, design, towelMap, maxTowelLength, emptyList())
        result
    }.size
}

fun part2(input: String): Long {
    val (towels, designs) = parseInput(input)
    val maxTowelLength = towels.maxOf { it.length }

    val towelMap = makeTowelMap(towels)

    return designs.sumOf { design ->
        val result = countPossibilities(design, design, towelMap, maxTowelLength)
        result
    }
}

fun makeTowelMap(towels: List<String>): Map<Any, Any> {
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

    return towelMap
}

val resultsCache = mutableMapOf<String, Boolean>()

fun isDesignPossible(
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
                isDesignPossible(
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

val resultsCache2 = mutableMapOf<String, Long>()

fun countPossibilities(
    originalDesign: String,
    subDesign: String,
    towelMap: Map<Any, Any>,
    maxTowelLength: Int,
): Long {
    if (subDesign in resultsCache2) {
        return resultsCache2[subDesign]!!
    }

    if (subDesign.isEmpty()) {
        resultsCache2[subDesign] = 1
        return 1
    } else {
        val towelsMatchingStart = getTowelsMatchingStart(subDesign, towelMap, maxTowelLength)
        return if (towelsMatchingStart.isEmpty()) {
            resultsCache2[subDesign] = 0
            0
        } else {
            towelsMatchingStart.sumOf { towel ->
                countPossibilities(
                    originalDesign = originalDesign,
                    subDesign = subDesign.substring(towel.length),
                    towelMap = towelMap,
                    maxTowelLength = maxTowelLength
                )
            }.also { resultsCache2[subDesign] = it }
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

fun parseInput(input: String): Pair<List<String>, List<String>> =
    input.split("\n\n").let { (first, second) ->
        Pair(first.split(", "), second.split("\n"))
    }
