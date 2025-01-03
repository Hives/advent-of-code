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
//    part2(input)
    trial(input)
//    part1(exampleInput2).checkAnswer(2024)

    exitProcess(0)

    time(message = "Part 1", warmUp = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(49520947122770)

}

fun part1(input: String): Long {
    val (initialInputs, gates) = parseInput(input)
    return run(initialInputs, gates).toLong(2)
}

/*
 This function prints out the code to generate a flowchart representing the
 logic-gate network represented by the input. My solution was to inspect it and
 work out which bits were wrong 😵
 */
fun part2(input: String): String {
    val (initialInputs, originalGates) = parseInput(input)

    val gates = applySwaps(originalGates)

    val gateNodes = gates.map { gate ->
        Node(
            id = gate.id,
            label = gate.javaClass.simpleName,
            output =
            if (gate.output.startsWith('z')) listOf("OUTPUT_${gate.output}")
            else gates.filter { gate.output in it.inputs }.map(Gate::id),
            outputWireName = gate.output
        )
    }

    val inputNodes = initialInputs.keys.sortedBy { "${it.substring(1)}${it.substring(0..1)}" }.map { wire ->
        Node(
            id = "INPUT_$wire",
            label = "INPUT_$wire",
            output = gates.filter { wire in it.inputs }.map(Gate::id),
            outputWireName = wire
        )
    }

    val outputNodes = gates.filter { gate ->
        gate.output.startsWith('z')
    }.map { gate ->
        Node(
            id = "OUTPUT_${gate.output}",
            label = "OUTPUT_${gate.output}",
            output = emptyList(),
            outputWireName = ""
        )
    }

    println("flowchart TD")
    val nodes = (inputNodes + gateNodes)
    nodes.forEach { node ->
        println("  ${node.id}[${node.label}\n${node.id}]")
    }
    nodes.forEach { node ->
        node.output.forEach { output ->
            println("  ${node.id}-->|${node.outputWireName}|$output")
        }
    }

    return ":("
}

fun applySwaps(gates: List<Gate>): List<Gate> {
    val swaps = listOf(
        Pair(201, 52),
        Pair(39, 49),
        Pair(48, 124),
        Pair(179, 206)
    )

    swaps.flatMap { swap ->
        listOf(
            gates.find { gate -> gate.rank == swap.first }!!.output,
            gates.find { gate -> gate.rank == swap.second }!!.output,
        )
    }.sorted().joinToString(",").also(::println)

    val swappedGates = gates.toMutableList()
    swaps.forEach { swap ->
        val gate1 = swappedGates.find { it.rank == swap.first }!!
        val gate2 = swappedGates.find { it.rank == swap.second }!!
        val gate1OriginalOutput = gate1.output
        gate1.output = gate2.output
        gate2.output = gate1OriginalOutput
    }

    return swappedGates
}

data class Node(
    val id: String,
    val label: String,
    val output: List<String>,
    val outputWireName: String
)

/*
 This function adds two binary numbers together using the logic-gate network.
 By turning individual bits in the numbers on and off you can investigate which
 bits don't add up correctly, which gives a clue as to where in the network
 the problems are
 */
fun trial(input: String): Int {
    val (_, originalGates) = parseInput(input)
    val initialInputs = (0..44).flatMap { n ->
        val nString = pad2(n)
        listOf("x$nString" to ZERO, "y$nString" to ZERO)
    }.toMap().toMutableMap()

    val gates = applySwaps(originalGates)

    //       444443333333333222222222211111111110000000000
    //       432109876543210987654321098765432109876543210
    // bad:  _____XX____________________X_________________
    val x = "100001000000000000000001111100000100000000111"
    val y = "100001000000000000100001111100000000000000101"

    x.toList().reversed().forEachIndexed { index, c ->
        initialInputs["x${pad2(index)}"] = if (c == '1') ONE else ZERO
    }
    val xBase10 = x.toLong(2)

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

    return Pair(initialInputs, gates)
}

sealed class Gate(
    open val inputs: List<String>,
    open var output: String,
    open val rank: Int
) {
    abstract fun process(in1: BinaryValue, in2: BinaryValue): BinaryValue

    val id: String
        get() = "GATE_$rank"

    override fun toString(): String {
        return "${inputs[0]} ${javaClass.simpleName} ${inputs[1]} -> $output"
    }

    data class AND(
        override val inputs: List<String>,
        override var output: String,
        override val rank: Int
    ) : Gate(inputs, output, rank) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 == ONE && in2 == ONE) ONE
            else ZERO

        override fun toString() = super.toString()
    }

    data class OR(
        override val inputs: List<String>,
        override var output: String,
        override val rank: Int
    ) : Gate(inputs, output, rank) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 == ZERO && in2 == ZERO) ZERO
            else ONE

        override fun toString() = super.toString()
    }

    data class XOR(
        override val inputs: List<String>,
        override var output: String,
        override val rank: Int
    ) : Gate(inputs, output, rank) {
        override fun process(in1: BinaryValue, in2: BinaryValue) =
            if (in1 != in2) ONE
            else ZERO

        override fun toString() = super.toString()
    }
}

enum class BinaryValue { ONE, ZERO }
