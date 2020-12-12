package days.day12

import kotlin.math.abs

fun doItPart2(input: List<String>, initial: State): State =
    input.fold(initial) { acc, s -> updateState(acc, s) }

fun updateState(state: State, input: String): State {
    val (action, value) = Pair(input.take(1), input.drop(1).toInt())
    val (location, waypoint) = state

    return when (action) {
        in "NESW" -> State(
            location = location,
            waypoint = waypoint + (Direction.valueOf(action).vector * value)
        )
        "R" -> State(
            location = location,
            waypoint = waypoint.rotateQuarterTurnsRight(Math.floorMod(value, 360) / 90)
        )
        "L" -> State(
            location = location,
            waypoint = waypoint.rotateQuarterTurnsRight(Math.floorMod(-value, 360) / 90)
        )
        "F" -> State(
            location = location + (waypoint * value),
            waypoint = waypoint
        )
        else -> throw Exception("Unparseable instruction: $input")
    }
}

enum class Direction(val vector: Vector) {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1 , 0))
}

data class Vector(val x: Int, val y: Int) {
    val manhattanDistance: Int
        get() = abs(x) + abs(y)

    operator fun times(n: Int) = Vector(x * n, y * n)

    operator fun plus(vector: Vector) = Vector(this.x + vector.x, this.y + vector.y)

    fun rotateQuarterTurnsRight(times: Int = 1) = this.repeatedlyApply(times) {
        Vector(
            y = -it.x,
            x = it.y
        )
    }
}

data class State(val location: Vector, val waypoint: Vector)

tailrec fun <T> T.repeatedlyApply(n: Int, f: (T) -> T): T =
    if (n == 0) this
    else f(this).repeatedlyApply(n - 1, f)
