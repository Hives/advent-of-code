package days.day02

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day02.txt").strings()

    time(iterations = 10_000, warmUpIterations = 100) {
        run(input, Submarine.Part1())
    }.checkAnswer(1660158)

    time(iterations = 10_000, warmUpIterations = 100) {
        run(input, Submarine.Part2())
    }.checkAnswer(1604592846)
}

fun run(instructions: List<String>, initial: Submarine) =
    instructions.map(::parseInstruction)
        .fold(initial) { current, (direction, units) ->
            when (direction) {
                Direction.FORWARD -> current.forward(units)
                Direction.DOWN -> current.down(units)
                Direction.UP -> current.up(units)
            }
        }
        .evaluate()

fun parseInstruction(instruction: String): Pair<Direction, Int> =
    instruction.split(" ")
        .let { (direction, units) ->
            Pair(Direction.valueOf(direction.uppercase()), units.toInt())
        }

enum class Direction { FORWARD, DOWN, UP }

sealed class Submarine(
    open val depth: Int,
    open val distance: Int
) {
    abstract fun forward(units: Int): Submarine
    abstract fun down(units: Int): Submarine
    abstract fun up(units: Int): Submarine

    fun evaluate() = this.depth * this.distance

    data class Part1(
        override val depth: Int = 0,
        override val distance: Int = 0
    ) : Submarine(depth, distance) {
        override fun forward(units: Int) = this.copy(distance = distance + units)
        override fun down(units: Int) = this.copy(depth = depth + units)
        override fun up(units: Int) = this.copy(depth = depth - units)
    }

    data class Part2(
        override val depth: Int = 0,
        override val distance: Int = 0,
        val aim: Int = 0
    ) : Submarine(depth, distance) {
        override fun forward(units: Int) = this.copy(
            distance = distance + units,
            depth = depth + (aim * units)
        )

        override fun down(units: Int) = this.copy(aim = aim + units)
        override fun up(units: Int) = this.copy(aim = aim - units)
    }
}

