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
    var total = 0
    input.indices.forEach { y ->
        input[y].indices.forEach { x ->
            if (grid.get(x, y).isDigit() && !grid.get(x - 1, y).isDigit()) {
                var i = x
                val numberList = mutableListOf<Char>()
                while (grid.get(i, y).isDigit()) {
                    numberList.add(grid.get(i, y))
                    i++
                }
                val number = numberList.joinToString("").toInt()
                val neighbours = ((x - 1)..i).toList()
                    .flatMap { listOf(Pair(it, y - 1), Pair(it, y + 1)) } +
                        Pair(x - 1, y) +
                        Pair(i, y)
                if (neighbours.any { (x, y) ->
                        val c = grid.get(x, y)
                        !c.isDigit() && c != '.'
                    }) {
                    total += number
                }
            }
        }
    }

    return total
}

fun part2(input: List<String>): Int {
    val grid = Grid(input)
    val starAdjacent = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    input.indices.forEach { y ->
        input[y].indices.forEach { x ->
            if (grid.get(x, y).isDigit() && !grid.get(x - 1, y).isDigit()) {
                var i = x
                val numberList = mutableListOf<Char>()
                while (grid.get(i, y).isDigit()) {
                    numberList.add(grid.get(i, y))
                    i++
                }
                val number = numberList.joinToString("").toInt()
                val neighbours = ((x - 1)..i).toList()
                    .flatMap { listOf(Pair(it, y - 1), Pair(it, y + 1)) } +
                        Pair(x - 1, y) +
                        Pair(i, y)
                neighbours.forEach { point ->
                    val (x, y) = point
                    if (grid.get(x, y) == '*') {
                       if (point !in starAdjacent) starAdjacent[point] = mutableListOf()
                        starAdjacent[point]?.add(number)
                    }
                }
            }
        }
    }

    return starAdjacent.values.filter { it.size == 2 }.sumOf { it[0] * it[1] }
}

class Grid(private val input: List<String>) {
    fun get(x: Int, y: Int): Char {
        if (y < 0 || y >= input.size) return '.'
        val row = input[y]
        if (x < 0 || x >= row.length) return '.'
        return row[x]
    }
}
