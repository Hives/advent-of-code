package days.day22_part2

import days.day22_part1.Instruction
import lib.Reader

enum class InputType { EXAMPLE, REAL }

fun main() {
    val input = Pair(Reader("day22.txt").string(), InputType.REAL)
    val exampleInput = Pair(Reader("day22-example.txt").string(), InputType.EXAMPLE)

    part2(exampleInput)
}

fun part2(input: Pair<String, InputType>) {
    parse(input)
}

typealias Grid = List<List<Char>>
fun Grid.p() = forEach { println(it.joinToString("")) }

data class Cube(val front: Grid, val right: Grid, val back: Grid, val left: Grid, val top: Grid, val bottom: Grid) {
    fun p() {
        println("\ntop:")
        top.p()
        println("\nfront:")
        front.p()
        println("\nright:")
        right.p()
        println("\nback:")
        back.p()
        println("\nleft:")
        left.p()
        println("\nbottom:")
        bottom.p()
    }
}

fun parse(inputAndType: Pair<String, InputType>): Pair<List<List<Char>>, List<Instruction>> {
    val (input, inputType) = inputAndType
    val squareSize = when (inputType) {
        InputType.EXAMPLE -> 4
        InputType.REAL -> 50
    }

    val (first, second) = input.split("\n\n")
    val map = first.lines().map { it.toList() }

    val rows = map.chunked(squareSize)
    val squares = rows.flatMap { rowOfSquares ->
        val n = (rowOfSquares.first().size + 1) / squareSize
        (0 until n).map { m ->
            rowOfSquares.map { row ->
                row.subList(m * squareSize, (m + 1) * squareSize)
            }
        }
    }.filter { it.first().first() != ' ' }

    val cube = when (inputType) {
        InputType.EXAMPLE -> {
            Cube(
                front = squares[0],
                right = squares[5].rotateCCW(1),
                back = squares[4].rotateCCW(2),
                left = squares[2].rotateCCW(3),
                top = squares[1].rotateCCW(2),
                bottom = squares[3],
            )
        }
        InputType.REAL -> TODO()
    }

    cube.p()

    squares.first().let {
        fun List<List<Char>>.p() {
            println()
            println()
            println()
            forEach { println(it.joinToString("")) }
        }
    }

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
