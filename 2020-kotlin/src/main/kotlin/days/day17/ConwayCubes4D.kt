package days.day17

class ConwayCubes4D(input: String) {
    private var state: MutableList<MutableList<MutableList<MutableList<Char>>>> =
        mutableListOf(
            mutableListOf(
                input.trim().split("\n").map { it.map { it }.toMutableList() }.toMutableList()
            )
        )
    private var bottomLayer = 0
    private var cycles = 0

    fun run(n: Int) {
        repeat(n) { crunch() }
    }

    fun crunch() {
        val newState =
            MutableList(state.size + 2) {
                MutableList(state[0].size + 2) {
                    MutableList(state[0][0].size + 2) {
                        MutableList(state[0][0][0].size + 2) { ' ' }
                    }
                }
            }

        for (w in 0 until state.size + 2) {
            for (z in 0 until state[0].size + 2) {
                for (y in 0 until state[0][0].size + 2) {
                    for (x in 0 until state[0][0][0].size + 2) {
                        newState[w][z][y][x] = applyRules(w - 1, z - 1, y - 1, x - 1)
                    }
                }
            }
        }
        state = newState
        bottomLayer--
        cycles++
    }

    private fun applyRules(w: Int, z: Int, y: Int, x: Int): Char {
        val activeNeighbours = getNeighbours(w, z, y, x).count { it == '#' }
        val currentState = state.getOrNull(w)?.getOrNull(z)?.getOrNull(y)?.getOrNull(x) ?: '.'

        return if (currentState == '#') {
            if (activeNeighbours == 2 || activeNeighbours == 3) '#'
            else '.'
        } else {
            if (activeNeighbours == 3) '#'
            else '.'
        }
    }

    fun countActive() =
        state.sumBy { cube -> cube.sumBy { slice -> slice.sumBy { col -> col.count { cell -> cell == '#' } } } }

    private fun getNeighbours(w: Int, z: Int, y: Int, x: Int) = directions4D
        .map { Point4D(it.w + w, it.z + z, it.y + y, it.x + x) }
        .map { state.getOrNull(it.w)?.getOrNull(it.z)?.getOrNull(it.y)?.getOrNull(it.x) ?: '.' }

    fun print() {
        println("After $cycles cycle${if (cycles != 1) "s" else ""}:\n")
        state.forEachIndexed { wIndex, cube ->
            cube.forEachIndexed { zIndex, slice ->
                println("w=${bottomLayer + wIndex}, z=${bottomLayer + zIndex}")
                slice.forEach { col ->
                    col.joinToString("").also { println(it) }
                }
                println()
            }
        }
    }
}

private data class Point4D(val w: Int, val z: Int, val y: Int, val x: Int)

private val range = listOf(-1, 0, 1)
private val directions4D =
    range.flatMap { w -> range.flatMap { z -> range.flatMap { y -> range.map { x -> Point4D(w, z, y, x) } } } }
        .filterNot { it == Point4D(0, 0, 0, 0) }
