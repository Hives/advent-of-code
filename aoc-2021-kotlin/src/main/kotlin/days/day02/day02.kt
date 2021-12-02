package days.day02

import lib.Reader

fun main() {
    val input = Reader("day02.txt").strings()

    input.fold(Position(0, 0)) { position, instruction ->
        """(.+) (\d+)""".toRegex().find(instruction)!!
            .destructured.let { (direction, valueString) ->
                val value = valueString.toInt()
                when (direction) {
                    "forward" -> position.forward(value)
                    "down" -> position.down(value)
                    "up" -> position.up(value)
                    else -> throw Exception("Unparseable instruction: $instruction")
                }
            }
    }.also { println(it.depth * it.horizontalPosition) }

    input.fold(StatePart2(0, 0, 0)) { state, instruction ->
        """(.+) (\d+)""".toRegex().find(instruction)!!
            .destructured.let { (direction, valueString) ->
                val value = valueString.toInt()
                when (direction) {
                    "forward" -> state.forward(value)
                    "down" -> state.down(value)
                    "up" -> state.up(value)
                    else -> throw Exception("Unparseable instruction: $instruction")
                }
            }
    }.also { println(it.depth * it.horizontalPosition) }
}

data class Position(val depth: Int, val horizontalPosition: Int) {
    fun forward(distance: Int) = this.copy(horizontalPosition = horizontalPosition + distance)
    fun down(distance: Int) = this.copy(depth = depth + distance)
    fun up(distance: Int) = this.copy(depth = depth - distance)
}

data class StatePart2(val depth: Int, val horizontalPosition: Int, val aim: Int) {
    fun down(units: Int) = this.copy(aim = aim + units)
    fun up(units: Int) = this.copy(aim = aim - units)
    fun forward(units: Int) = this.copy(
        horizontalPosition = horizontalPosition + units,
        depth = depth + (aim * units)
    )
}
