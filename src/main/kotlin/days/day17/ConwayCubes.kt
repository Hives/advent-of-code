package days.day17

import lib.Reader

val example = ".#.\n" +
        "..#\n" +
        "###"

fun main() {
    val puzzleInput = Reader("day17.txt").string()
//    val c = ConwayCubes(example)
    val c = ConwayCubes(puzzleInput)
    c.crunch()
    c.crunch()
    c.crunch()
    c.crunch()
    c.crunch()
    c.crunch()
    c.countActive().also { println(it) }
}

class ConwayCubes(input: String) {
    private var state: MutableList<MutableList<MutableList<Char>>> =
        mutableListOf(input.trim().split("\n").map { it.map { it }.toMutableList() }.toMutableList())
    private var bottomLayer = 0
    private var cycles = 0

    fun crunch() {
        val newState =
            MutableList(state.size + 2) { MutableList(state[0].size + 2) { MutableList(state[0][0].size + 2) { ' ' } } }
        for (z in 0 until state.size + 2) {
            for (y in 0 until state[0].size + 2) {
                for (x in 0 until state[0][0].size + 2) {
                    newState[z][y][x] = applyRules(x - 1, y - 1, z - 1)
                }
            }
        }
        state = newState
        bottomLayer--
        cycles++
    }

    fun applyRules(x: Int, y: Int, z: Int): Char {
        val activeNeighbours = getNeighbours(x, y, z).count { it == '#' }
        val currentState = state.getOrNull(z)?.getOrNull(y)?.getOrNull(x) ?: '.'

        return if (currentState == '#') {
            if (activeNeighbours == 2 || activeNeighbours == 3) '#'
            else '.'
        } else {
            if (activeNeighbours == 3) '#'
            else '.'
        }
    }

    fun countActive() = state.sumBy { slice -> slice.sumBy { col -> col.count { cell -> cell == '#' } } }

    private fun getNeighbours(x: Int, y: Int, z: Int) = directions
        .map { Point(it.x + x, it.y + y, it.z + z) }
        .map { state.getOrNull(it.z)?.getOrNull(it.y)?.getOrNull(it.x) ?: '.' }

    fun print() {
        println("After $cycles cycle${if (cycles != 1) "s" else ""}:\n")
        state.forEachIndexed { index, slice ->
            println("z=${bottomLayer + index}")
            slice.forEach { row ->
                row.joinToString("").also { println(it) }
            }
            println()
        }
    }
}

data class Point(val x: Int, val y: Int, val z: Int)

val directions = listOf(
    Point(-1, -1, 1),
    Point(-1, 0, 1),
    Point(-1, 1, 1),
    Point(0, -1, 1),
    Point(0, 0, 1),
    Point(0, 1, 1),
    Point(1, -1, 1),
    Point(1, 0, 1),
    Point(1, 1, 1),
    Point(-1, -1, 0),
    Point(-1, 0, 0),
    Point(-1, 1, 0),
    Point(0, -1, 0),
    Point(0, 1, 0),
    Point(1, -1, 0),
    Point(1, 0, 0),
    Point(1, 1, 0),
    Point(-1, -1, -1),
    Point(-1, 0, -1),
    Point(-1, 1, -1),
    Point(0, -1, -1),
    Point(0, 0, -1),
    Point(0, 1, -1),
    Point(1, -1, -1),
    Point(1, 0, -1),
    Point(1, 1, -1),
)
