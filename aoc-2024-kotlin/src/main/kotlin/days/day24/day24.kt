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

//    part1(input).checkAnswer(49520947122770)
    part2(input)
//    part1(exampleInput2).checkAnswer(2024)

    exitProcess(0)

    time(message = "Part 1", warmUp = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(49520947122770)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: String): Long {
    val (initialInputs, gates) = parseInput(input)
    return run(initialInputs, gates).toLong(2)
}

fun part2(input: String): String {
    trial(input)

    return ":("
}

fun trial(input: String): Int {
    val (_, gates) = parseInput(input)
    val initialInputs = (0..44).flatMap { n ->
        val nString = pad2(n)
        listOf("x$nString" to ZERO, "y$nString" to ZERO)
    }.toMap().toMutableMap()

    // bad:  _____X_____________________X_________________
    val x = "100000000000000000000000000000000000000000000"
    x.toList().reversed().forEachIndexed { index, c ->
        initialInputs["x${pad2(index)}"] = if (c == '1') ONE else ZERO
    }
    val xBase10 = x.toLong(2)

    val y = "100000000000000000000000000000000000000000000"
    y.toList().reversed().forEachIndexed { index, c ->
        initialInputs["y${pad2(index)}"] = if (c == '1') ONE else ZERO
    }
    val yBase10 = y.toLong(2)

    val output = run(initialInputs, gates).toList().dropWhile { it == '0' }.joinToString(separator = "")

    output.checkAnswer((xBase10 + yBase10).toString(2))

    return -1
}

fun pad2(n: Int) = if (n < 10) "0$n" else "$n"

fun run(initialInputs: Map<String, BinaryValue>, gates: List<Gate>): String {
    val wireValues = initialInputs.toMutableMap()

    val unprocessedGates = gates.toMutableList()

    while (unprocessedGates.isNotEmpty()) {
        val gate = unprocessedGates.first {
            it.inputs[0] in wireValues && it.inputs[1] in wireValues
        }
        wireValues[gate.output] =
            gate.process(wireValues[gate.inputs[0]]!!, wireValues[gate.inputs[1]]!!)
        unprocessedGates.remove(gate)
    }

    return wireValues.toList()
        .filter { it.first.startsWith('z') }
        .sortedBy { it.first }
        .reversed()
        .map { if (it.second == ONE) '1' else '0' }
        .joinToString("")
}

fun parseInput(input: String): Pair<Map<String, BinaryValue>, List<Gate>> {
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

    val gates = second.lines().mapIndexed { index, line ->
        val r = """(.+) (AND|OR|XOR) (.+) -> (.+)""".toRegex()
        r.find(line)!!.groupValues.let { (_, in1, type, in2, out) ->
            val inputs = listOf(in1, in2).sorted()
            when (type) {
                "AND" -> Gate.AND(inputs, out, index)
                "OR" -> Gate.OR(inputs, out, index)
                "XOR" -> Gate.XOR(inputs, out, index)
                else -> throw Error("Unknown gate type: $type")
            }
        }
    }

//    gates.sortedBy { it.toString() }.forEachIndexed { index, gate -> println("$index: $gate") }

    return Pair(initialInputs, gates)
}

sealed class Gate(
    open val inputs: List<String>,
    open val output: String,
    open val id: Int
) {
    abstract fun process(in1: BinaryValue, in2: BinaryValue): BinaryValue

    override fun toString(): String {
        return "${inputs[0]} ${javaClass.simpleName} ${inputs[1]} -> $output"
    }

    data class AND(
        override val inputs: List<String>,
        override val output: String,
        override val id: Int
    ) : Gate(inputs, output, id) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 == ONE && in2 == ONE) ONE
            else ZERO

        override fun toString() = super.toString()
    }

    data class OR(
        override val inputs: List<String>,
        override val output: String,
        override val id: Int
    ) : Gate(inputs, output, id) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 == ZERO && in2 == ZERO) ZERO
            else ONE

        override fun toString() = super.toString()
    }

    data class XOR(
        override val inputs: List<String>,
        override val output: String,
        override val id: Int
    ) : Gate(inputs, output, id) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 != in2) ONE
            else ZERO

        override fun toString() = super.toString()
    }
}

enum class BinaryValue { ONE, ZERO }
