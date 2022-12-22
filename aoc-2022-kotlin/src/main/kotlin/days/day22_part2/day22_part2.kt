package days.day22_part2

import days.day22_part1.Instruction
import lib.Reader
import lib.Vector
import lib.checkAnswer

fun main() {
    val input = Pair(Reader("day22.txt").string(), InputType.Real)
    val exampleInput = Pair(Reader("day22-example.txt").string(), InputType.Example)

    part2(exampleInput).checkAnswer(5031)
    part2(input).checkAnswer(162038)
}

fun part2(input: Pair<String, InputType>): Int {
    val (_, inputType) = input
    val (map, instructions) = parse(input)

    val initial = Vector(
        x = map[0].indexOfFirst { it == '.' },
        y = 0
    )

    val warpZonesHit = mutableSetOf<WarpZone>()
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

    fun forward1(current: Vector, facing: Vector): Pair<Vector, Vector> {
        updatePath(current, facing)

        val target = current + facing

        val (nextTarget, nextFacing) = when (map.at(target)) {
            '.' -> Pair(target, facing)
            '#' -> Pair(current, facing)
            else -> {
                val warpZone = inputType.warpZones.single { target in it.points && facing == it.inFacing }
                warpZonesHit.add(warpZone)
                val warpedTarget = warpZone.pointTransform(target)
                val next = when (map.at(warpedTarget)) {
                    '.' -> Pair(warpedTarget, warpZone.outFacing)
                    '#' -> Pair(current, facing)
                    else -> throw Exception("Couldn't find a place?")
                }
                next
            }
        }

        return Pair(nextTarget, nextFacing)
    }

    tailrec fun forward(distance: Int, current: Vector, facing: Vector): Pair<Vector, Vector> {
        return if (distance == 0) Pair(current, facing)
        else {
            val (nextLocation, nextFacing) = forward1(current, facing)
            forward(distance - 1, nextLocation, nextFacing)
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
            is Instruction.Forward -> forward(instruction.distance, current, facing)
            is Instruction.Turn -> {
                val newFacing = when (instruction) {
                    Instruction.Turn.Left -> facing.rotateRight() // LOL
                    Instruction.Turn.Right -> facing.rotateLeft() // LOL
                }
                Pair(current, newFacing)
            }
        }
    }
    updatePath(finalLocation, finalFacing)

    printy()

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

sealed class InputType(val warpZones: List<WarpZone>) {
    object Example : InputType(
        listOf(
            WarpZone(
                id = 1,
                points = (8..11).map { Vector(it, -1) }.toSet(),
                pointTransform = { v -> Vector(11 - v.x, 4) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Down.v
            ),
            WarpZone(
                id = 2,
                points = (0..3).map { Vector(12, it) }.toSet(),
                pointTransform = { v -> Vector(15, 11 - v.y) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Left.v,
            ),
            WarpZone(
                id = 3,
                points = (4..7).map { Vector(12, it) }.toSet(),
                pointTransform = { v -> Vector(19 - v.y, 8) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Down.v,
            ),
            WarpZone(
                id = 4,
                points = (12..15).map { Vector(it, 7) }.toSet(),
                pointTransform = { v -> Vector(11, 19 - v.x) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Left.v,
            ),
            WarpZone(
                id = 5,
                points = (8..11).map { Vector(16, it) }.toSet(),
                pointTransform = { v -> Vector(11, 11 - v.y) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Left.v,
            ),
            WarpZone(
                id = 6,
                points = (12..15).map { Vector(it, 12) }.toSet(),
                pointTransform = { v -> Vector(0, 19 - v.x) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Right.v,
            ),
            WarpZone(
                id = 7,
                points = (8..11).map { Vector(it, 12) }.toSet(),
                pointTransform = { v -> Vector(11 - v.x, 7) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Up.v,
            ),
            WarpZone(
                id = 8,
                points = (8..11).map { Vector(7, it) }.toSet(),
                pointTransform = { v -> Vector(15 - v.y, 7) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Up.v,
            ),
            WarpZone(
                id = 9,
                points = (4..7).map { Vector(it, 8) }.toSet(),
                pointTransform = { v -> Vector(8, 15 - v.x) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Right.v,
            ),
            WarpZone(
                id = 10,
                points = (0..3).map { Vector(it, 8) }.toSet(),
                pointTransform = { v -> Vector(11 - v.x, 11) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Up.v,
            ),
            WarpZone(
                id = 11,
                points = (4..7).map { Vector(-1, it) }.toSet(),
                pointTransform = { v -> Vector(19 - v.y, 11) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Up.v,
            ),
            WarpZone(
                id = 12,
                points = (0..3).map { Vector(it, 3) }.toSet(),
                pointTransform = { v -> Vector(11 - v.x, 0) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Down.v,
            ),
            WarpZone(
                id = 13,
                points = (4..7).map { Vector(it, 3) }.toSet(),
                pointTransform = { v -> Vector(8, v.x - 4) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Right.v,
            ),
            WarpZone(
                id = 14,
                points = (0..3).map { Vector(7, it) }.toSet(),
                pointTransform = { v -> Vector(v.y + 4, 4) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Down.v,
            )
        )
    )

    object Real : InputType(
        listOf(
            WarpZone(
                id = 1,
                points = (50..99).map { Vector(it, -1) }.toSet(),
                pointTransform = { v -> Vector(0, v.x + 100) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Right.v
            ),
            WarpZone(
                id = 2,
                points = (100..149).map { Vector(it, -1) }.toSet(),
                pointTransform = { v -> Vector(v.x - 100, 199) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Up.v
            ),
            WarpZone(
                id = 3,
                points = (0..49).map { Vector(150, it) }.toSet(),
                pointTransform = { v -> Vector(99, 149 - v.y) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Left.v
            ),
            WarpZone(
                id = 4,
                points = (100..149).map { Vector(it, 50) }.toSet(),
                pointTransform = { v -> Vector(99, v.x - 50) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Left.v
            ),
            WarpZone(
                id = 5,
                points = (50..99).map { Vector(100, it) }.toSet(),
                pointTransform = { v -> Vector(v.y + 50, 49) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Up.v
            ),
            WarpZone(
                id = 6,
                points = (100..149).map { Vector(100, it) }.toSet(),
                pointTransform = { v -> Vector(149, 149 - v.y) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Left.v
            ),
            WarpZone(
                id = 7,
                points = (50..99).map { Vector(it, 150) }.toSet(),
                pointTransform = { v -> Vector(49, v.x + 100) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Left.v
            ),
            WarpZone(
                id = 8,
                points = (150..199).map { Vector(50, it) }.toSet(),
                pointTransform = { v -> Vector(v.y - 100, 149) },
                inFacing = Unit.Right.v,
                outFacing = Unit.Up.v
            ),
            WarpZone(
                id = 9,
                points = (0..49).map { Vector(it, 200) }.toSet(),
                pointTransform = { v -> Vector(v.x + 100, 0) },
                inFacing = Unit.Down.v,
                outFacing = Unit.Down.v
            ),
            WarpZone(
                id = 10,
                points = (150..199).map { Vector(-1, it) }.toSet(),
                pointTransform = { v -> Vector(v.y - 100, 0) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Down.v
            ),
            WarpZone(
                id = 11,
                points = (100..149).map { Vector(-1, it) }.toSet(),
                pointTransform = { v -> Vector(50, 149 - v.y) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Right.v
            ),
            WarpZone(
                id = 12,
                points = (0..49).map { Vector(it, 99) }.toSet(),
                pointTransform = { v -> Vector(50, v.x + 50) },
                inFacing = Unit.Up.v,
                outFacing = Unit.Right.v
            ),
            WarpZone(
                id = 13,
                points = (50..99).map { Vector(49, it) }.toSet(),
                pointTransform = { v -> Vector(v.y - 50, 100) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Down.v
            ),
            WarpZone(
                id = 14,
                points = (0..49).map { Vector(49, it) }.toSet(),
                pointTransform = { v -> Vector(0, 149 - v.y) },
                inFacing = Unit.Left.v,
                outFacing = Unit.Right.v
            ),
        )
    )
}

data class WarpZone(
    val id: Int,
    val points: Set<Vector>,
    val pointTransform: (Vector) -> Vector,
    val inFacing: Vector,
    val outFacing: Vector
)

fun parse(inputAndType: Pair<String, InputType>): Pair<List<List<Char>>, List<Instruction>> {
    val (input, inputType) = inputAndType

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

fun <T> List<List<T>>.rotateCCW(n: Int): List<List<T>> =
    if (n == 0) this
    else this.rotateCCW().rotateCCW(n - 1)

fun <T> List<List<T>>.rotateCCW(): List<List<T>> =
    indices.map { y ->
        get(y).indices.map { x ->
            this[x][size - y - 1]
        }
    }

sealed class Unit(val v: Vector) {
    object Up : Unit(Vector(0, -1))
    object Down : Unit(Vector(0, 1))
    object Left : Unit(Vector(-1, 0))
    object Right : Unit(Vector(1, 0))
}
