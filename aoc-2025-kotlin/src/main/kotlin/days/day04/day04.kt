package days.day04

import kotlin.system.exitProcess
import lib.Grid
import lib.Reader
import lib.Vector
import lib.at
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day04/input.txt").grid()
    val (part1, part2) = Reader("/day04/answers.txt").ints()
    val exampleInput = Reader("/day04/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(grid: Grid<Char>) =
    getAccessible(grid).count()

fun part2(grid: Grid<Char>): Int {
    val mutableGrid = grid.map { it.toMutableList() }

    var removed = 0
    while (true) {
        val accessible = getAccessible(mutableGrid)
        if (accessible.count() == 0) break
        accessible.forEach { (point, _) ->
            removed += 1
            mutableGrid[point.y][point.x] = '.'
        }
    }
    
    return removed
}

fun getAccessible(grid: Grid<Char>): Sequence<Pair<Vector, Char>> =
    grid.cells()
        .filter { it.second == '@' }
        .filter { (point, cell) ->
            cell == '@' && point.surrounding.count { grid.at(it, '.') == '@' } < 4
        }
