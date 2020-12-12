package days.day12

import java.lang.Math.floorMod
import kotlin.math.abs

fun doItPart2(instructions: List<String>, initial: State): State =
    instructions.fold(initial) { acc, instruction -> updateState(acc, instruction) }

fun updateState(state: State, input: String): State {
    val (location, waypoint) = state
    val action = input.take(1)
    val value = input.drop(1).toInt()

    return when (action) {
        in "NESW" -> State(
            location = location,
            waypoint = waypoint + (UnitVector.valueOf(action).vector * value)
        )
        "R" -> State(
            location = location,
            waypoint = waypoint.rotateQuarterTurnsRight(floorMod(value, 360) / 90)
        )
        "L" -> State(
            location = location,
            waypoint = waypoint.rotateQuarterTurnsRight(floorMod(-value, 360) / 90)
        )
        "F" -> State(
            location = location + (waypoint * value),
            waypoint = waypoint
        )
        else -> throw Exception("Unparseable instruction: $input")
    }
}

enum class UnitVector(val vector: Vector) {
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
