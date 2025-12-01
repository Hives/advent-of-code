package days.day01

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day01/input.txt").strings()
    val exampleInput = Reader("/day01/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<String>) =
    input.fold(Pair(50, 0)) { (p, count), command ->
        val n = command.drop(1).toInt()
        val pNew = (if (command[0] == 'L') p - n else p + n).mod(100)
        val countNew = if (pNew == 0) count + 1 else count
        Pair(pNew, countNew)
    }.second

fun part2(input: List<String>) =
    input.fold(Pair(50, 0)) { (p, count), command ->
        var n = command.drop(1).toInt()
        var pNew = p
        var countNew = count
        while (n > 0) {
            pNew += if (command[0] == 'L') -1 else 1
            pNew = pNew.mod(100)
            if (pNew == 0) countNew += 1
            n -= 1
        }
        Pair(pNew, countNew)
    }.second
