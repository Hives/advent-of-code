package days.day20

import lib.Reader
import lib.time

fun main() {
    val example = Reader("day20-part1-example.txt").string()
    val part1Input = Reader("day20-part1.txt").string()

    time("part 1 (should be 12519494280967)", 1, 1) {
        val c = Puzzle(part1Input)
        c.go()
        c.multiplyCorners()
    }

    // generate part 2 input
    val c = Puzzle(part1Input)
    c.go()
    c.placements.toList().toImage(c.dimension).forEach { println(it) }

    val part2Input = Reader("day20-part2.txt").strings()

    time("Part 2 (should be 2442)", 100) {
        val numberOfSeaMonsters =
            getAllOrientations(part2Input)
                .map { it.countSeaMonsters() }.first { it != 0 }

        val numberOfHashes = part2Input.sumBy { row -> row.count { char -> char == '#' } }
        val numberOfHashesInASeaMonster = 15
        val numberOfHashesWhichArentPartOfASeaMonster =
            numberOfHashes - (numberOfHashesInASeaMonster * numberOfSeaMonsters)

        numberOfHashesWhichArentPartOfASeaMonster
    }
}
