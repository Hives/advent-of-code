package days.day12

import java.lang.Math.floorMod
import kotlin.math.abs

fun doItPart2(instructions: List<String>, initial: State) = instructions.fold(initial, ::updateState)

fun updateState(state: State, input: String) = Instruction.from(input).update(state)

sealed class Instruction {
    abstract fun update(initial: State): State

    class MoveWaypoint(private val direction: UnitVector, private val distance: Int) : Instruction() {
        override fun update(initial: State): State = with(initial) {
            State(
                location = location,
                waypoint = waypoint + (direction.vector * distance)
            )
        }
    }

    class RotateWaypoint(private val quarterTurnsRight: Int) : Instruction() {
        override fun update(initial: State): State = with(initial) {
            State(
                location = location,
                waypoint = waypoint.rotateQuarterTurnsRight(quarterTurnsRight)
            )
        }
    }

    class TowardWaypoint(private val multiplier: Int) : Instruction() {
        override fun update(initial: State): State = with(initial) {
            State(
                location = location + (waypoint * multiplier),
                waypoint = waypoint
            )
        }
    }

    companion object {
        fun from(input: String): Instruction {
            val action = input.take(1)
            val value = input.drop(1).toInt()

            return when (action) {
                in "NESW" -> MoveWaypoint(
                    direction = UnitVector.valueOf(action),
                    distance = value
                )
                "R" -> RotateWaypoint(degreesToQuarterTurnsRight(value))
                "L" -> RotateWaypoint(degreesToQuarterTurnsRight(-value))
                "F" -> TowardWaypoint(value)
                else -> throw Exception("Unparsable instruction: $input")
            }
        }
    }
}

private fun degreesToQuarterTurnsRight(degrees: Int) = floorMod(degrees, 360) / 90

enum class UnitVector(val vector: Vector) {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1, 0))
}

data class Vector(val x: Int, val y: Int) {
    val manhattanDistance: Int
        get() = abs(x) + abs(y)

    operator fun times(n: Int) = Vector(
        x = x * n,
        y = y * n
    )

    operator fun plus(vector: Vector) = Vector(
        x = this.x + vector.x,
        y = this.y + vector.y
    )

    fun rotateQuarterTurnsRight(times: Int = 1) = this.repeatedlyApply(times) {
        Vector(
            x = it.y,
            y = -it.x
        )
    }
}

data class State(val location: Vector, val waypoint: Vector)

tailrec fun <T> T.repeatedlyApply(n: Int, f: (T) -> T): T =
    if (n == 0) this
    else f(this).repeatedlyApply(n - 1, f)
