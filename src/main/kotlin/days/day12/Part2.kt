package days.day12

import kotlin.math.abs

fun List<Instruction2>.doIt(initial: State2): State2 =
    this.fold(initial) { acc, instruction -> instruction.move(acc) }

fun parseInput2(input: String) = input.trim().lines().map { Instruction2.from(it) }

sealed class Instruction2 {
    abstract fun move(initial: State2): State2

    data class North(private val arg: Int) : Instruction2() {
        override fun move(initial: State2) = initial.copy(waypointNorth = initial.waypointNorth + arg)
    }

    data class South(private val arg: Int) : Instruction2() {
        override fun move(initial: State2) = initial.copy(waypointNorth = initial.waypointNorth - arg)
    }

    data class East(private val arg: Int) : Instruction2() {
        override fun move(initial: State2) = initial.copy(waypointEast = initial.waypointEast + arg)
    }

    data class West(private val arg: Int) : Instruction2() {
        override fun move(initial: State2) = initial.copy(waypointEast = initial.waypointEast - arg)
    }

    data class Right(private val quarterTurns: Int) : Instruction2() {
        override fun move(initial: State2) = initial.repeatedlyApply(quarterTurns) { it.rotateWaypointRight() }
    }

    data class Left(private val quarterTurns: Int) : Instruction2() {
        override fun move(initial: State2) = initial.repeatedlyApply(quarterTurns) { it.rotateWaypointLeft() }
    }

    data class Forward(private val arg: Int) : Instruction2() {
        override fun move(initial: State2) = initial.copy(
            north = initial.north + (arg * initial.waypointNorth),
            east = initial.east + (arg * initial.waypointEast)
        )
    }

    companion object {
        fun from(input: String): Instruction2 {
            val (action, value) = Pair(input.take(1), input.drop(1).toInt())
            return when (action) {
                "N" -> North(value)
                "S" -> South(value)
                "E" -> East(value)
                "W" -> West(value)
                "L" -> Left(Math.floorMod(value, 360) / 90)
                "R" -> Right(Math.floorMod(value, 360) / 90)
                "F" -> Forward(value)
                else -> throw Exception("Unparseable instruction")
            }
        }
    }
}

data class State2(val east: Int, val north: Int, val waypointEast: Int, val waypointNorth: Int) {
    val manhattanDistance: Int
        get() = abs(east) + abs(north)

    fun rotateWaypointLeft() = this.copy(
        waypointNorth = this.waypointEast,
        waypointEast = -this.waypointNorth
    )

    fun rotateWaypointRight() = this.copy(
        waypointNorth = -this.waypointEast,
        waypointEast = this.waypointNorth
    )
}

tailrec fun <T> T.repeatedlyApply(n: Int, f: (T) -> T): T =
    if (n == 0) this
    else f(this).repeatedlyApply(n - 1, f)
