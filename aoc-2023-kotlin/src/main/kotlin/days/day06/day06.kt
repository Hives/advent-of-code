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

    time(message = "Part 2", iterations = 1, warmUpIterations = 0) {
        part2(input)
    }.checkAnswer(42515755)
}

fun part1(input: List<String>): Int {
    val races = parse(input)
    return races.map { race ->
        (0..(race.time)).map { holdTime ->
            val travelTime = race.time - holdTime
            travelTime * holdTime
        }.count { it > race.record }
    }.reduce(Int::times)
}

fun part2(input: List<String>): Int {
    val race = parse2(input)
    return (0..(race.time)).map { holdTime ->
        val travelTime = race.time - holdTime
        travelTime * holdTime
    }.count { it > race.record }
}

fun parse(input: List<String>): List<Race> {
    fun parseLine(input: String) =
        input.split(Regex(""":\s+"""))[1].split(Regex("""\s+""")).map(String::toLong)

    val times = parseLine(input[0])
    val distances = parseLine(input[1])
    return times.zip(distances).map { Race(it.first, it.second) }
}

fun parse2(input: List<String>): Race {
    fun parseLine(input: String) =
        input.split(Regex(""":\s+"""))[1].replace(Regex("""\s+"""), "").toLong()
    return Race(
        time = parseLine(input[0]),
        record = parseLine(input[1])
    )
}

data class Race(val time: Long, val record: Long)
