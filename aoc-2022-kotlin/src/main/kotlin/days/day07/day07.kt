package days.day07

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day07.txt").strings()
    val exampleInput = Reader("day07-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 5000) {
        part1(input)
    }.checkAnswer(1844187)

    time(message = "Part 2", warmUpIterations = 5000) {
        part2(input)
    }.checkAnswer(4978279)
}

fun part1(input: List<String>) =
    buildFolderSizes(input).values.filter { it <= 100_000 }.sum()

fun part2(input: List<String>): Int {
    val folderSizes = buildFolderSizes(input)

    val totalSpace = 70_000_000
    val desiredSpace = 30_000_000
    val freeSpace = totalSpace - folderSizes[listOf("/")]!!

    return folderSizes.values.filter { freeSpace + it >= desiredSpace }.min()
}

fun buildFolderSizes(input: List<String>): Map<List<String>, Int> {
    val pwd = mutableListOf<String>()
    val folderSizes = mutableMapOf<List<String>, Int>()

    input.forEach { line ->
        when {
            line == "$ cd .." -> pwd.removeLast()
            line.startsWith("$ cd") -> pwd.add(line.split(" ").last())
            line.first().isDigit() -> {
                val size = line.split(" ").first().toInt()
                pwd.indices.map { i ->
                    val path = pwd.subList(0, i + 1).toList()
                    folderSizes[path] = folderSizes.getOrDefault(path, 0) + size
                }
            }
        }
    }

    return folderSizes
}
