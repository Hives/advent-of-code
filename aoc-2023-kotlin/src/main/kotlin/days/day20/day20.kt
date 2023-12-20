package days.day20

import days.day20.Module.Broadcast
import days.day20.Module.Conjunction
import days.day20.Module.FlipFlop
import days.day20.Pulse.HIGH
import days.day20.Pulse.LOW
import days.day20.State.OFF
import days.day20.State.ON
import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day20/input.txt").strings()
    val example1 = Reader("/day20/example-1.txt").strings()
    val example2 = Reader("/day20/example-2.txt").strings()


    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(670984704)

    /*
    Part 2:
    ran the `generateMermaidCode` function to print out my map in a format that can be rendered
    as a flow graph by https://mermaid.live (see flow-chart.png)

    by inspecting the graph you can see that rx will return a LOW pulse on the same turn that the
    four nodes tc, ks, dn and ms all issue a LOW pulse.

    so we run the simulation for a while and keep a record of on what turns those nodes send out
    a LOW pulse. turns out they each send out LOW pulses at a regular frequency. the answer then
    is those four periods multiplied together.
     */
}

fun part1(input: List<String>): Long {
    val moduleMap = parse(input)

    var lowPulseCount = 0L
    var highPulseCount = 0L

    repeat(1000) {
        val unevaluated = mutableListOf(Triple("button", "broadcaster", LOW))
        while (unevaluated.isNotEmpty()) {
            val (from, to, pulse) = unevaluated.removeAt(0)
            if (pulse == LOW) lowPulseCount++ else highPulseCount++
            val module = moduleMap[to]
            if (module != null) {
                val next = module.pulse(pulse, from)
                unevaluated.addAll(next)
            }
        }
    }

    return lowPulseCount * highPulseCount
}

fun part2(input: List<String>): Long {
    val moduleMap = parse(input)

    var buttonCount = 0L

    while (true) {
        val unevaluated = mutableListOf(Triple("button", "broadcaster", LOW))
        buttonCount += 1
        while (unevaluated.isNotEmpty()) {
            val (from, to, pulse) = unevaluated.removeAt(0)
            val module = moduleMap[to]
            if (module != null) {
                val next = module.pulse(pulse, from)
                listOf("tc", "ks", "dn", "ms").forEach { interesting ->
                    if (interesting == module.name) {
                        val sent = next.firstOrNull()?.third
                        if (sent == LOW) {
                            println("$buttonCount, $interesting, $sent")
                        }
                    }
                }
                unevaluated.addAll(next)
            }
        }
    }


    return buttonCount
}

fun generateMermaidCode(lines: List<String>) {
    lines.forEach {
        val (start, end) = it.split(" -> ")
        val name = if (start == "broadcaster") start else start.drop(1)
        val type = when (start[0]) {
            '%' -> "ff"
            '&' -> "conj"
            else -> "whatever"
        }
        end.split(", ")
            .forEach { out ->
                when {
                    type == "ff" -> println("$name{{$name}} --> $out")
                    type == "conj" -> println("$name([$name]) --> $out")
                    else -> println("$name[$start] --> $out")
                }
            }
    }
}

fun parse(lines: List<String>): Map<String, Module> {
    val moduleMap = lines.map(::parse).associateBy { it.name }.toMutableMap()
    moduleMap.values.forEach { module ->
        module.outputs.forEach { outputName ->
            val outputModule = moduleMap[outputName]
            if (outputModule is Conjunction) {
                outputModule.pulse(LOW, module.name)
            }
        }
    }
    return moduleMap
}

fun parse(line: String): Module {
    val r = Regex("""(.+) -> (.+)""")
    val match = r.find(line)!!
    val (start, end) = match.destructured
    val outputs = end.split(", ")
    if (start == "broadcaster") {
        return Broadcast("broadcaster", outputs)
    }
    val name = start.drop(1)
    if (start[0] == '%') {
        return FlipFlop(name, outputs, OFF)
    }
    return Conjunction(name, outputs, mutableMapOf())
}

sealed class Module(open val name: String, open val outputs: List<String>) {
    abstract fun pulse(input: Pulse, from: String): List<Triple<String, String, Pulse>>

    data class FlipFlop(
        override val name: String,
        override val outputs: List<String>,
        var state: State
    ) : Module(name, outputs) {
        override fun pulse(input: Pulse, from: String): List<Triple<String, String, Pulse>> {
            if (input == LOW) {
                if (state == ON) {
                    state = OFF
                    return outputs.map { Triple(name, it, LOW) }
                } else {
                    state = ON
                    return outputs.map { Triple(name, it, HIGH) }
                }
            } else {
                return emptyList()
            }
        }
    }

    data class Conjunction(
        override val name: String,
        override val outputs: List<String>,
        val previousInputs: MutableMap<String, Pulse>
    ) : Module(name, outputs) {
        override fun pulse(input: Pulse, from: String): List<Triple<String, String, Pulse>> {
            previousInputs[from] = input
            val output = if (previousInputs.values.all { it == HIGH }) LOW else HIGH
            return outputs.map { Triple(name, it, output) }
        }
    }

    data class Broadcast(
        override val name: String,
        override val outputs: List<String>,
    ) : Module(name, outputs) {
        override fun pulse(pulse: Pulse, from: String) =
            outputs.map { Triple(name, it, pulse) }
    }
}

enum class State { ON, OFF }
enum class Pulse { LOW, HIGH }

fun Map<String, Pulse>.update(name: String, pulse: Pulse) =
    (keys + name).toSet().associate { key ->
        if (key == name) key to pulse
        else key to this[key]!!
    }
