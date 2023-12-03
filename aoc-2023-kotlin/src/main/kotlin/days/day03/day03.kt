package days.day03

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day03/input.txt").strings()
    val exampleInput = Reader("/day03/example-1.txt").strings()

    time(message = "Part 1") { part1(input) }
        .checkAnswer(528799)

    time(message = "Part 2") { part2(input) }
        .checkAnswer(84907174)
}

fun part1(input: List<String>): Int {
    val grid = Grid(input)
    return getNumbersAndNeighbours(grid)
        .filter { (_, neighbours) -> neighbours.any { grid.get(it).isSymbol() } }
        .sumOf { it.first }
}

fun part2(input: List<String>): Int {
    val grid = Grid(input)
    val starAdjacentNumbers = mutableMapOf<Point, MutableList<Int>>()
    getNumbersAndNeighbours(grid).forEach { (number, neighbours) ->
        neighbours.forEach { point ->
            if (grid.get(point) == '*') {
                if (point !in starAdjacentNumbers) starAdjacentNumbers[point] = mutableListOf()
                starAdjacentNumbers[point]?.add(number)
            }
        }
    }
    return starAdjacentNumbers.values.filter { it.size == 2 }.sumOf { it[0] * it[1] }
}

fun getNumbersAndNeighbours(grid: Grid): List<Pair<Int, List<Pair<Int, Int>>>> {
    val numbersAndNeighbours = mutableListOf<Pair<Int, List<Point>>>()
    (0..grid.maxY).forEach { y ->
        (0..grid.maxX).forEach { x ->
            if (grid.get(x, y).isDigit() && !grid.get(x - 1, y).isDigit()) {
                var i = x
                val digits = mutableListOf<Char>()
                while (grid.get(i, y).isDigit()) {
                    digits.add(grid.get(i, y))
                    i++
                }
                val number = digits.joinToString("").toInt()
                val neighbours = ((x - 1)..i).toList()
                    .flatMap { listOf(Pair(it, y - 1), Pair(it, y + 1)) } +
                        Pair(x - 1, y) +
                        Pair(i, y)
                numbersAndNeighbours.add(Pair(number, neighbours))
            }
        }
    }
    return numbersAndNeighbours
}

fun Char.isSymbol() = !isDigit() && this != '.'

class Grid(private val input: List<String>) {
    fun get(point: Point): Char =
        this.get(point.first, point.second)

    fun get(x: Int, y: Int): Char {
        if (y < 0 || y >= input.size) return '.'
        val row = input[y]
        if (x < 0 || x >= row.length) return '.'
        return row[x]
    }

    val maxX: Int
        get() = input[0].length - 1
    val maxY: Int
        get() = input.size - 1
}

typealias Point = Pair<Int, Int>
