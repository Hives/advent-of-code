package days.day14

import lib.Reader
import lib.time

fun main() {
    val program = Reader("day14.txt").strings()

    time("part 1", 100) {
        val computer = BitMaskComputerPart1(program)
        computer.run()
        computer.sumOfValues
    }

    time("part 2", 100) {
        val computer2 = BitMaskComputerPart2(program)
        computer2.run()
        computer2.sumOfValues
    }
}
