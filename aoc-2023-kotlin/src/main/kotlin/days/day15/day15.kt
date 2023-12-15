package days.day15

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day15/input.txt").commaSeparated()
    val exampleInput = Reader("/day15/example-1.txt").commaSeparated()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(513643)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(265345)
}

fun part1(input: List<String>) =
    input.sumOf(::hashAlgo)

fun part2(input: List<String>): Int {
    val instructions = input.map(Instruction::from)
    val boxes = (0..255).associateWith { mutableListOf<Lens>() }
    
    instructions.forEach { instruction ->
        val box = boxes[instruction.box]!!
        when (instruction) {
            is Instruction.Dash -> {
                box.removeIf { it.label == instruction.label }
            }

            is Instruction.Equals -> {
                val existingLensIndex = box.indexOfFirst { it.label == instruction.label }
                val lens = Lens(instruction.label, instruction.focalLength)
                if (existingLensIndex == -1) box.add(lens)
                else box[existingLensIndex] = lens
            }
        }
    }
    return boxes.focusingPower()
}

fun Map<Int, List<Lens>>.focusingPower(): Int =
    toList().fold(0) { acc, (box, lenses) ->
        if (lenses.isEmpty()) acc
        else {
            acc + lenses.mapIndexed { index, lens ->
                (box + 1) * (index + 1) * lens.focalLength
            }.sum()
        }
    }

fun hashAlgo(s: String) =
    s.fold(0) { acc, c ->
        ((acc + c.code) * 17) % 256
    }

data class Lens(val label: String, val focalLength: Int)

sealed class Instruction(open val label: String, open val box: Int) {
    data class Dash(override val label: String, override val box: Int) : Instruction(label, box)
    data class Equals(override val label: String, override val box: Int, val focalLength: Int) : Instruction(label, box)
    companion object {
        fun from(s: String): Instruction {
            val dashMatch = Regex("""([a-z]+)-""").find(s)
            if (dashMatch != null) {
                val (label) = dashMatch.destructured
                return Dash(label, hashAlgo(label))
            }
            val equalsMatch = Regex("""([a-z]+)=(\d+)""").find(s)!!
            val (label, focalLength) = equalsMatch.destructured
            return Equals(label, hashAlgo(label), focalLength.toInt())
        }
    }
}
