package days.day13

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day13.txt").strings()
    val timestamp = input[0].toInt()
    val busTimetable = input[1].split(",")
    val busIds = busTimetable.filter { it != "x" }.map { it.toInt() }

    time("part 1") { doPart1(timestamp, busIds) }

    val exampleTimetable = "7,13,x,x,59,x,31,19".split(",")

    solveBusTimetable(exampleTimetable)

    time("part 2") {
        solveBusTimetable(busTimetable)
    }
}