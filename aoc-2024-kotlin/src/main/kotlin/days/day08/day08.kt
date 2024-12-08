package days.day08

import lib.Grid
import lib.Reader
import lib.Vector
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day08/input.txt").grid()
    val exampleInput = Reader("/day08/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(390)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(1246)
}

fun part1(grid: Grid<Char>) =
    solve(grid, ::findAntinodes1)

fun part2(grid: Grid<Char>) =
    solve(grid, ::findAntinodes2)

fun solve(grid: Grid<Char>, findAntinodes: FindAntinodes): Int {
    val antennaGroups = grid.findAntennas().groupBy { it.frequency }.values

    val antinodeLocations = antennaGroups.flatMap { antennas ->
        antennas.pairs().flatMap {
            findAntinodes(it.first, it.second, grid)
        }
    }.distinct()

    return antinodeLocations.size
}

typealias FindAntinodes = (Antenna, Antenna, Grid<Char>) -> List<Vector>

fun findAntinodes1(antenna1: Antenna, antenna2: Antenna, grid: Grid<Char>): List<Vector> {
    val d = antenna2.location - antenna1.location
    return listOf(antenna1.location - d, antenna2.location + d)
        .filter { it.withinBounds(grid) }
}

fun findAntinodes2(antenna1: Antenna, antenna2: Antenna, grid: Grid<Char>): List<Vector> {
    val d = antenna2.location - antenna1.location
    val antinodes = mutableListOf<Vector>()
    var antinode = antenna1.location
    while (true) {
        if (antinode.withinBounds(grid)) {
            antinodes.add(antinode)
            antinode -= d
        } else break
    }
    antinode = antenna2.location
    while (true) {
        if (antinode.withinBounds(grid)) {
            antinodes.add(antinode)
            antinode += d
        } else break
    }
    return antinodes
}

fun <T> List<T>.pairs(): List<Pair<T, T>> =
    flatMapIndexed { index, t1 ->
        subList(index + 1, size).map { t2 ->
            Pair(t1, t2)
        }
    }

fun Grid<Char>.findAntennas() =
    cells()
        .mapNotNull { (point, char) -> if (char != '.') Antenna(char, point) else null }
        .toList()

fun Vector.withinBounds(grid: Grid<*>) =
    y >= 0 && y < grid.size && x >= 0 && x < grid[y].size

data class Antenna(val frequency: Char, val location: Vector)
