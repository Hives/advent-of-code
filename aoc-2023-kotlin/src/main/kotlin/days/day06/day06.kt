package days.day06

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day06/input.txt").strings()
    val exampleInput = Reader("/day06/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(500346)

//    time(message = "Part 2") {
//        part2(input)
//    }.checkAnswer(0)
}

fun part1(input: List<String>): Int {
    val races = parse(input)
    return races.map { race ->
        (0..(race.time)).map { holdTime ->
            race.calculateDistance(holdTime)
        }.count { it > race.record }
    }.reduce(Int::times)
}

fun part2(input: List<String>): Int {
    return -1
}

fun parse(input: List<String>): List<Race> {
    fun parseLine(input: String) =
        input.split(Regex(""":\s+"""))[1].split(Regex("""\s+""")).map(String::toInt)
    val times = parseLine(input[0])
    val distances = parseLine(input[1])
    return times.zip(distances).map { Race(it.first, it.second) }
}

data class Race(val time: Int, val record: Int) {
    fun calculateDistance(holdTime: Int): Int {
        val travelTime = time - holdTime
        return travelTime * holdTime
    }
}
