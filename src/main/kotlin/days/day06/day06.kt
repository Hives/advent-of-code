package days

import lib.Reader

fun main() {
    val input = Reader("day06.txt").string()

    val part1 = input
        .split("\n\n")
        .map {
            it.replace("\n", "").toSet().size
        }.sum()

    println(part1)

    val letters = "abcdefghijklmnopqrstuvwxyz"

    val part2 = input
            .split("\n\n")
            .map { it.split("\n") }
            .map { group ->
                letters.count { letter ->
                    group.all { it.contains(letter) }
                }
            }.sum()


    println(part2)
}
