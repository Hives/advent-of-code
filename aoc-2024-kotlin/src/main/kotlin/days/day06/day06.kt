package days.day06

import lib.Grid
import lib.Reader
import lib.Vector
import lib.at
import lib.atOrNull
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day06/input.txt").grid()
    val exampleInput = Reader("/day06/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(4964)

    // slow ~ 1m
    time(message = "Part 2", iterations = 1, warmUp = 0) {
        part2(input)
    }.checkAnswer(1740)
}

fun part1(grid: Grid<Char>) =
    getPath(grid).map { it.position }.toSet().size

fun part2(grid: Grid<Char>): Int {
    val path = getPath(grid)

    val start = grid.cells().find { (_, char) -> char == '^' }!!.first
    val initialState = State(start, Vector(0, -1))

    return path.map { it.position + it.facing }.distinct().count { pathLoops(grid.addObstacle(it), initialState) }
}

fun getPath(grid: Grid<Char>): List<State> {
    var position = grid.cells().find { (_, char) -> char == '^' }!!.first
    var state = State(position, Vector(0, -1))
    val path = mutableListOf<State>()

    while (grid.atOrNull(state.position) != null) {
        path.add(state)

        val nextPosition = state.position + state.facing
        state = if (grid.at(nextPosition, '.') != '#') {
            state.copy(position = nextPosition)
        } else {
            state.copy(facing = state.facing.rotateRight())
        }
    }

    return path
}

fun pathLoops(grid: Grid<Char>, start: State): Boolean {
    val path = mutableListOf<State>()
    var state = start

    while (grid.atOrNull(state.position) != null) {
        if (state in path) {
//            printy(grid, path.map { it.position }.toSet())
            return true
        }

        path.add(state)

        val nextPosition = state.position + state.facing
        state = if (listOf('.', '^').contains(grid.at(nextPosition, '.'))) {
            state.copy(position = nextPosition)
        } else {
            state.copy(facing = state.facing.rotateRight())
        }
    }

    return false
}

fun Grid<Char>.addObstacle(v: Vector) =
    mapIndexed { y, row ->
        row.mapIndexed { x, char ->
            if (Vector(x, y) == v) 'O' else char
        }
    }

fun printy(grid: Grid<Char>, path: Set<Vector>) {
    grid.forEachIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (Vector(x, y) in path) 'X' else c
        }.joinToString("").also(::println)
    }
    println()
}

data class State(
    val position: Vector,
    val facing: Vector
)
