package days.day24

import days.day24.BinaryValue.ONE
import days.day24.BinaryValue.ZERO
import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day24/input.txt").string()
    val exampleInput1 = Reader("/day24/example-1.txt").string()
    val exampleInput2 = Reader("/day24/example-2.txt").string()

//    part1(exampleInput1).checkAnswer(4)
//    part1(exampleInput2).checkAnswer(2024)

    time(message = "Part 1", warmUp = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(49520947122770)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: String): Long {
    val (initialInputs, gates, _) = parseInput(input)

    val wireValues = initialInputs.toMutableMap()

    val unprocessedGates = gates.toMutableList()

    while (unprocessedGates.isNotEmpty()) {
        val gate = unprocessedGates.first {
            it.inputs.first in wireValues && it.inputs.second in wireValues
        }
        wireValues[gate.output] =
            gate.process(wireValues[gate.inputs.first]!!, wireValues[gate.inputs.second]!!)
        unprocessedGates.remove(gate)
    }

    return wireValues.toList()
        .filter { it.first.startsWith('z') }
        .sortedBy { it.first }
        .reversed()
        .map { if (it.second == ONE) '1' else '0' }
        .joinToString("")
        .toLong(2)
}

fun part2(input: String): Int {
    return -1
}

fun parseInput(input: String): Triple<Map<String, BinaryValue>, List<Gate>, Map<String, Map<String, List<Gate>>>> {
    val (first, second) = input.split("\n\n")

    val initialInputs = first.lines().map { line ->
        val (id, value) = line.split(": ")
        Pair(
            id,
            when (value.toInt()) {
                1 -> ONE
                0 -> ZERO
                else -> throw Error("Invalid input value (expected 0 or 1): $value")
            }
        )
    }.toMap()

    val gates = second.lines().map { line ->
        val r = """(.+) (AND|OR|XOR) (.+) -> (.+)""".toRegex()
        r.find(line)!!.groupValues.let { (_, in1, type, in2, out) ->
            when (type) {
                "AND" -> Gate.AND(Pair(in1, in2), out)
                "OR" -> Gate.OR(Pair(in1, in2), out)
                "XOR" -> Gate.XOR(Pair(in1, in2), out)
                else -> throw Error("Unknown gate type: $type")
            }
        }
    }

    val wires = gates.flatMap { listOf(it.inputs.first, it.inputs.second, it.output) }.distinct().sorted()

    val gateMap = wires.associateWith { wire1 ->
        wires.associateWith { wire2 ->
            gates.filter { it.inputs == Pair(wire1, wire2) || it.inputs == Pair(wire2, wire1) }
        }.filter { it.value.isNotEmpty() }
    }.filter { it.value.isNotEmpty() }

//    gateMap.toList().sortedBy { it.first }.forEach { println(it) }

    return Triple(initialInputs, gates, gateMap)
}

sealed class Gate(open val inputs: Pair<String, String>, open val output: String) {
    abstract fun process(in1: BinaryValue, in2: BinaryValue): BinaryValue

    data class AND(override val inputs: Pair<String, String>, override val output: String) : Gate(inputs, output) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 == ONE && in2 == ONE) ONE
            else ZERO
    }

    data class OR(override val inputs: Pair<String, String>, override val output: String) : Gate(inputs, output) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 == ZERO && in2 == ZERO) ZERO
            else ONE
    }

    data class XOR(override val inputs: Pair<String, String>, override val output: String) : Gate(inputs, output) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 != in2) ONE
            else ZERO
    }
}

enum class BinaryValue { ONE, ZERO }
