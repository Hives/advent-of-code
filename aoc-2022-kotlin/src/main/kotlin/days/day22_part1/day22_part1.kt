package days.day22_part1

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day22.txt").string()
    val exampleInput = Reader("day22-example.txt").string()

    time(message = "Part 1", warmUpIterations = 3, iterations = 10) {
        part1(input)
    }.checkAnswer(106094)
}

fun part1(input: String): Int {
    val (map, instructions) = parse(input)
    val initial = Vector(
        x = map[0].indexOfFirst { it == '.' },
        y = 0
    )

    val path = mutableMapOf<Vector, Char>()

    fun updatePath(place: Vector, facing: Vector?) {
        path[place] = when (facing) {
            null -> 'O'
            Vector(0, 1) -> 'v'
            Vector(0, -1) -> '^'
            Vector(1, 0) -> '>'
            Vector(-1, 0) -> '<'
            else -> throw Exception("Bad facing: $facing")
        }
    }

    fun forward1(current: Vector, facing: Vector): Vector {
        updatePath(current, facing)

        val target = current + facing
        val next = when (map.at(target)) {
            '.' -> target
            '#' -> current
            else -> {
                val wrappedTarget = when (facing) {
                    Unit.Down.v -> {
                        val newY = map.indices.first { map.at(Vector(target.x, it)) in listOf('.', '#') }
                        Vector(target.x, newY)
                    }

                    Unit.Up.v -> {
                        val newY = map.indices.last { map.at(Vector(target.x, it)) in listOf('.', '#') }
                        Vector(target.x, newY)
                    }

                    Unit.Right.v -> {
                        val newX = map[target.y].indices.first { map.at(Vector(it, target.y)) in listOf('.', '#') }
                        Vector(newX, target.y)
                    }

                    Unit.Left.v -> {
                        val newX = map[target.y].indices.last { map.at(Vector(it, target.y)) in listOf('.', '#') }
                        Vector(newX, target.y)
                    }

                    else -> throw Exception("Bad facing direction $facing")
                }
                when (map.at(wrappedTarget)) {
                    '.' -> wrappedTarget
                    '#' -> current
                    else -> throw Exception("Couldn't find a place?")
                }
            }
        }

        return next
    }

    tailrec fun forward(distance: Int, current: Vector, facing: Vector): Vector {
        return if (distance == 0) current
        else {
            forward(distance - 1, forward1(current, facing), facing)
        }
    }

    fun printy() {
        map.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                val v = Vector(x, y)
                if (v in path) path[v]
                else cell
            }.joinToString("")
        }.forEach(::println)
    }


    val (finalLocation, finalFacing) = instructions.fold(
        Pair(
            initial,
            Unit.Right.v
        )
    ) { (current, facing), instruction ->
        when (instruction) {
            is Instruction.Forward -> Pair(forward(instruction.distance, current, facing), facing)
            Instruction.Turn.Left -> Pair(current, facing.rotateRight())
            Instruction.Turn.Right -> {
                val newFacing = facing.rotateLeft()
                Pair(current, newFacing)
            }
        }
    }
    updatePath(finalLocation, finalFacing)

    val finalFacingScore = when (finalFacing) {
        Vector(0, -1) -> 3
        Vector(0, 1) -> 1
        Vector(1, 0) -> 0
        Vector(-1, 0) -> 2
        else -> throw Exception("Bad final facing $finalFacing")
    }
    return ((finalLocation.y + 1) * 1000) + ((finalLocation.x + 1) * 4) + finalFacingScore
}

fun <T> List<List<T>>.at(v: Vector): T? = try {
    this[v.y][v.x]
} catch (e: Exception) {
    null
}


fun parse(input: String): Pair<List<List<Char>>, List<Instruction>> {
    val (first, second) = input.split("\n\n")
    val map = first.lines().map { it.toList() }

    val (instructions, remainder) = second.fold(Pair(emptyList<Instruction>(), emptyList<Char>())) { acc, nextChar ->
        val (instructions, numbers) = acc
        if (nextChar == 'L' || nextChar == 'R') {
            val forward = Instruction.Forward.from(numbers)
            val turn = Instruction.Turn.from(nextChar)
            val newInstructions = instructions + forward + turn
            Pair(newInstructions, emptyList())
        } else {
            Pair(instructions, numbers + nextChar)
        }
    }
    val finalInstructions =
        if (remainder.isNotEmpty()) instructions + Instruction.Forward.from(remainder) else instructions

    return Pair(map, finalInstructions)
}

sealed class Unit(val v: Vector) {
    object Up : Unit(Vector(0, -1))
    object Down : Unit(Vector(0, 1))
    object Left : Unit(Vector(-1, 0))
    object Right : Unit(Vector(1, 0))
}

sealed class Instruction {
    data class Forward(val distance: Int) : Instruction() {
        companion object {
            fun from(cs: List<Char>) =
                Forward(cs.joinToString("").toInt())
        }
    }

    sealed class Turn(private val ccwQuarterTurns: Int) : Instruction() {
        object Left : Turn(1)
        object Right : Turn(-1)

        companion object {
            fun from(c: Char) =
                when (c) {
                    'L' -> Left
                    'R' -> Right
                    else -> throw Exception("Oh no")
                }
        }
    }
}
