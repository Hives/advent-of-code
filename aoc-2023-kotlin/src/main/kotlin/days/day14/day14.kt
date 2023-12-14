package days.day14

import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.repeatedlyApply
import lib.time

fun main() {
    val input = Reader("/day14/input.txt").grid()
    val exampleInput = Reader("/day14/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(108614)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(96447)
}

fun part1(input: Grid) =
    input.tiltNorth().score()

fun part2(input: Grid): Int {
    var current = input
    val states = mutableListOf(current)
    while (true) {
        current = current.spinCycle()
        if (states.contains(current)) break
        states.add(current)
    }
    val loopPoint = states.indexOf(current)
    val loopSize = states.size - loopPoint
    val final = states[((1_000_000_000 - loopPoint) % loopSize) + loopPoint]
    return final.score()
}

fun Grid.spinCycle() =
    repeatedlyApply(4) { it.tiltNorth().rotateRight() }

fun Grid.tiltNorth(): Grid {
    val newGrid = toMutable()
    newGrid.indices.forEach { y ->
        newGrid[y].indices.forEach { x ->
            val start = Vector(x, y)
            if (newGrid.at(start) == 'O') {
                var location = start
                val up = Vector(0, -1)
                while (newGrid.at(location + up) == '.') location += up
                if (start != location) {
                    newGrid[start.y][start.x] = '.'
                    newGrid[location.y][location.x] = 'O'
                }
            }
        }
    }
    return newGrid
}

fun Grid.rotateRight() =
    this[0].indices.map { y ->
        this.indices.reversed().map { x ->
            this[x][y]
        }
    }

fun Grid.score() =
    reversed().mapIndexed { index, row -> (index + 1) * row.count { it == 'O' }  }.sum()

fun Grid.printy() =
    forEach { println(it.joinToString("")) }

fun Grid.toMutable() =
    map { it.toMutableList() }.toMutableList()

fun Grid.at(v: Vector) =
    if (v.x < 0 || v.x >= this[0].size || v.y < 0 || v.y >= this.size) '#'
    else this[v.y][v.x]
