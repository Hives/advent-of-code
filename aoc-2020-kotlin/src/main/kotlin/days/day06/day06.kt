package days.day06

import lib.Reader

fun main() {
    val input = Reader("day06.txt").string().split("\n\n")

    val part1 = input.sumBy { it.replace("\n", "").toSet().size }

    println(part1)

    val part2 = input
            .map { it.split("\n") }
            .sumBy { group ->
                ('a'..'z').count { letter ->
                    group.all { it.contains(letter) }
                }
            }

    println(part2)

    val part1again = input
        .sumBy {
            it.split("\n")
                .map(String::toSet)
                .reduce { acc, list -> acc.union(list) }
                .size
        }

    println(part1again)

    val part2again = input
        .sumBy {
            it.split("\n")
                .map(String::toSet)
                .reduce { acc, list -> acc.intersect(list) }
                .size
        }

    println(part2again)
}
