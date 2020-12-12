package days.day12

import kotlin.math.abs

fun List<Instruction1>.doIt(initial: State1): State1 =
    this.fold(initial) { acc, instruction -> instruction.move(acc) }

fun parseInput1(input: String) = input.trim().lines().map { Instruction1.from(it) }

sealed class Instruction1 {
    abstract fun move(initial: State1): State1

    data class North(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) = initial.copy(north = initial.north + arg)
    }

    data class South(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) = initial.copy(north = initial.north - arg)
    }

    data class East(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) = initial.copy(east = initial.east + arg)
    }

    data class West(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) = initial.copy(east = initial.east - arg)
    }

    data class Right(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) = initial.copy(dir = Math.floorMod(initial.dir + arg, 360))
    }

    data class Left(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) = initial.copy(dir = Math.floorMod(initial.dir - arg, 360))
    }

    data class Forward(private val arg: Int) : Instruction1() {
        override fun move(initial: State1) =
            when (initial.dir) {
                0 -> North(arg).move(initial)
                90 -> East(arg).move(initial)
                180 -> South(arg).move(initial)
                270 -> West(arg).move(initial)
                else -> throw Exception("Unrecognised direction: ${initial.dir}")
            }
    }

    companion object {
        fun from(input: String): Instruction1 {
            val (action, value) = Pair(input.take(1), input.drop(1).toInt())
            return when (action) {
                "N" -> North(value)
                "S" -> South(value)
                "E" -> East(value)
                "W" -> West(value)
                "L" -> Left(value)
                "R" -> Right(value)
                "F" -> Forward(value)
                else -> throw Exception("Unparseable instruction")
            }
        }
    }
}

data class State1(val east: Int, val north: Int, val dir: Int) {
    val manhattanDistance: Int
        get() = abs(east) + abs(north)
}
