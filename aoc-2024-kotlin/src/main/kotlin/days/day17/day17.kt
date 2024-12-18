package days.day17

import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day17/input.txt").string()
    val exampleInput1 = Reader("/day17/example-1.txt").string()
    val exampleInput2 = Reader("/day17/example-2.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer("7,5,4,3,4,5,3,4,6")

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(164278899142333)
}

fun part1(input: String) =
    Computer.from(input)
        .run()
        .output.joinToString(",")

fun part2(input: String): Any {
    val computer = Computer.from(input)
    var allAVals = listOf(List(computer.program.size) { 1 })

    var pointer = 0

    while (pointer < computer.program.size) {
        allAVals = allAVals.flatMap { aVals ->
            (0..7).map { n ->
                val newAvals = aVals.toMutableList()
                newAvals[pointer] = n
                newAvals
            }
        }
            .filter { aVals ->
                val a = aVals.joinToString("").toLong(8)
                computer.reset().setA(a).run()

                if (computer.program == computer.output) return a

                computer.program[computer.program.size - pointer - 1] == computer.output[computer.output.size - pointer - 1]
            }

        pointer += 1
    }

    return -1
}

data class Computer(val program: List<Long>, val initialA: Long, val initialB: Long, val initialC: Long) {
    var pointer = 0L
    var a = initialA
    var b = initialB
    var c = initialC
    val output = mutableListOf<Long>()

    fun run(): Computer {
        while (pointer < program.size) step()
        return this
    }

    fun reset(): Computer {
        a = initialA
        b = initialB
        c = initialC
        pointer = 0
        output.removeAll { true }
        return this
    }

    fun setA(value: Long): Computer {
        a = value
        return this
    }

    fun printy() {
        println("a: $a, b: $b, c: $c, program: $program, output: $output")
    }

    private fun step() {
        val opcode = program[pointer.toInt()]
        val operand = program[pointer.toInt() + 1]
        when (opcode) {
            0L -> adv(operand)
            1L -> bxl(operand)
            2L -> bst(operand)
            3L -> jnz(operand)
            4L -> bxc()
            5L -> out(operand)
            6L -> bdv(operand)
            7L -> cdv(operand)
            else -> {
                throw Error("Unknown opcode: $opcode")
            }
        }
    }

    private fun adv(operand: Long) {
        val operandValue = comboOperand(operand)
        a /= power(2, operandValue)
        pointer += 2
    }

    private fun bxl(operand: Long) {
        b = b xor operand
        pointer += 2
    }

    private fun bst(operand: Long) {
        val operandValue = comboOperand(operand)
        b = operandValue.mod(8L)
        pointer += 2
    }

    private fun jnz(operand: Long) {
        if (a != 0L) {
            pointer = operand
        } else {
            pointer += 2
        }
    }

    private fun bxc() {
        b = b xor c
        pointer += 2
    }

    private fun out(operand: Long) {
        val operandValue = comboOperand(operand)
        output.add(operandValue.mod(8L))
        pointer += 2
    }

    private fun bdv(operand: Long) {
        val operandValue = comboOperand(operand)
        b = a / power(2, operandValue)
        pointer += 2
    }

    private fun cdv(operand: Long) {
        val operandValue = comboOperand(operand)
        c = a / power(2, operandValue)
        pointer += 2
    }

    private fun comboOperand(operand: Long): Long =
        when (operand) {
            0L, 1L, 2L, 3L -> operand
            4L -> a
            5L -> b
            6L -> c
            else -> {
                throw Error("Invalid combo operand: $operand")
            }
        }

    companion object {
        fun from(input: String): Computer {
            val (first, second) = input.split("\n\n")
            val registers = first.lines().map { line -> line.split(": ")[1].toLong() }
            val program = second.split(": ")[1].split(",").map { it.toLong() }
            return Computer(program, registers[0], registers[1], registers[2])
        }
    }
}

fun power(base: Long, exponent: Long): Long {
    var result = 1L
    var count = exponent
    while (count > 0L) {
        result *= base
        count -= 1
    }
    return result
}
