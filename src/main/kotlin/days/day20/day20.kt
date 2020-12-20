package days.day20

import lib.Reader

fun main() {
    val example = Reader("day20-example.txt").string()
    val puzzleInput = Reader("day20.txt").string()

    val c = Puzzle(puzzleInput)
    c.go()
    println(c.multiplyCorners())
}

val myExample = """Tile 1:
##.#
...#
#...
...#

Tile 2:
#..#
....
#...
#...

Tile 3:
#..#
....
....
#.##

Tile 4:
#..#
####
.###
####
"""
