package days.day07

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day07.txt").strings()
    val exampleInput = Reader("day07-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 1000) {
        part1(input)
    }.checkAnswer(1844187)

    time(message = "Part 2", warmUpIterations = 1000) {
        part2(input)
    }.checkAnswer(4978279)
}

fun part1(input: List<String>) =
    buildFolderSizes(input).values.filter { it <= 100_000 }.sum()

fun part2(input: List<String>): Int {
    val folderSizes = buildFolderSizes(input)

    val totalSpace = 70000000
    val desiredSpace = 30000000
    val freeSpace = totalSpace - folderSizes[listOf("/")]!!

    return folderSizes.values.filter { freeSpace + it >= desiredSpace }.min()
}

fun buildFolderSizes(input: List<String>): Map<List<String>, Int> {
    val pwd = mutableListOf<String>()
    val folderSizes = mutableMapOf<List<String>, Int>()

    input.forEach { line ->
        val segments = line.split(" ")
        when (segments[0]) {
            "$" -> when (segments[1]) {
                "cd" -> when (segments[2]) {
                    ".." -> pwd.removeLast()
                    else -> pwd.add(segments[2])
                }
            }

            else -> {
                when (segments[0]) {
                    "dir" -> Unit
                    else -> pwd.indices.forEach { i ->
                        val p = pwd.subList(0, i + 1).toList()
                        folderSizes[p] = folderSizes.getOrDefault(p, 0) + segments[0].toInt()
                    }
                }
            }
        }
    }

    return folderSizes
}
