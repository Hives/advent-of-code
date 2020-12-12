package days.day12

import kotlin.math.abs

fun List<Instruction2>.doIt(initial: State2): State2 =
    this.fold(initial) { acc, instruction -> instruction.move(acc) }

fun parseInput2(input: String) = input.trim().lines().map { Instruction2.from(it) }

sealed class Instruction2 {
    abstract fun move(initial: State2): State2

    data class North(private val distance: Int) : Instruction2() {
        override fun move(initial: State2) = with(initial) {
            State2(
                boat = boat,
                waypoint = waypoint.move(Vector(0, distance))
            )
        }
    }

    data class East(private val distance: Int) : Instruction2() {
        override fun move(initial: State2) = with(initial) {
            State2(
                boat = boat,
                waypoint = waypoint.move(Vector(distance, 0))
            )
        }
    }

    data class Turn(private val clockwiseQuarterTurns: Int) : Instruction2() {
        override fun move(initial: State2) = with(initial) {
            State2(
                boat = boat,
                waypoint = waypoint.repeatedlyApply(clockwiseQuarterTurns) {it.rotateRight()}
            )
        }
    }

    data class Forward(private val arg: Int) : Instruction2() {
        override fun move(initial: State2) = with(initial) {
            State2(
                boat = boat.move(waypoint * arg),
                waypoint = waypoint
            )
        }
    }

    companion object {
        fun from(input: String): Instruction2 {
            val (action, value) = Pair(input.take(1), input.drop(1).toInt())
            return when (action) {
                "N" -> North(value)
                "S" -> North(-value)
                "E" -> East(value)
                "W" -> East(-value)
                "R" -> Turn(Math.floorMod(value, 360) / 90)
                "L" -> Turn(Math.floorMod(-value, 360) / 90)
                "F" -> Forward(value)
                else -> throw Exception("Unparseable instruction")
            }
        }
    }
}

data class Vector(val east: Int, val north: Int) {
    val manhattanDistance: Int
        get() = abs(east) + abs(north)

    operator fun times(n: Int) = Vector(east * n, north * n)

    fun move(vector: Vector) = Vector(this.east + vector.east, this.north + vector.north)

    fun rotateRight() = this.copy(
        north = -this.east,
        east = this.north
    )
}

data class State2(val boat: Vector, val waypoint: Vector)

tailrec fun <T> T.repeatedlyApply(n: Int, f: (T) -> T): T =
    if (n == 0) this
    else f(this).repeatedlyApply(n - 1, f)
