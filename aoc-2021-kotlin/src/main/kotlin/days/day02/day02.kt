package days.day02

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day02.txt").strings()

    time(iterations = 10_000, warmUpIterations = 100) {
        Submarine.Part1(0, 0).applyInstructions(input)
    }

    time(iterations = 10_000, warmUpIterations = 100) {
        Submarine.Part2(0, 0, 0).applyInstructions(input)
    }
}

sealed class Submarine(
    open val depth: Int,
    open val distance: Int
) {
    fun applyInstructions(instructions: List<String>) =
        instructions.fold(this) { current, instruction ->
            instruction.split(" ").let { (direction, unitsString) ->
                val units = unitsString.toInt()
                when (direction) {
                    "forward" -> current.forward(units)
                    "down" -> current.down(units)
                    "up" -> current.up(units)
                    else -> throw Exception("Unparseable instruction: $instruction")
                }
            }
        }.evaluate()

    abstract fun forward(units: Int): Submarine
    abstract fun down(units: Int): Submarine
    abstract fun up(units: Int): Submarine

    private fun evaluate() = this.depth * this.distance

    data class Part1(
        override val depth: Int,
        override val distance: Int
    ) : Submarine(depth, distance) {
        override fun forward(units: Int) = this.copy(distance = distance + units)
        override fun down(units: Int) = this.copy(depth = depth + units)
        override fun up(units: Int) = this.copy(depth = depth - units)
    }

    data class Part2(
        override val depth: Int,
        override val distance: Int,
        val aim: Int
    ) : Submarine(depth, distance) {
        override fun forward(units: Int) = this.copy(
            distance = distance + units,
            depth = depth + (aim * units)
        )
        override fun down(units: Int) = this.copy(aim = aim + units)
        override fun up(units: Int) = this.copy(aim = aim - units)
    }
}

