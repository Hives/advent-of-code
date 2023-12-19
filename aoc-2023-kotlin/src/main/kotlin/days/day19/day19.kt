package days.day19

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day19/input.txt").string()
    val exampleInput = Reader("/day19/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(386787)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: String): Int {
    val (workflows, parts) = parse(input)
    val acceptedParts = parts.filter { part ->
        var currentWorkflow = workflows["in"]!!
        while(true) {
            val outcome = currentWorkflow.test(part)
            when (outcome) {
                Outcome.Accept -> return@filter true
                Outcome.Reject -> return@filter false
                is Outcome.ToWorkflow -> currentWorkflow = workflows[outcome.name]!!
            }
        }
        throw Error("omgwtfbbq")
    }
    return acceptedParts.sumOf(Part::score)
}

fun part2(input: String): Int {
    return -1
}

fun parse(input: String): Pair<Map<String, Workflow>, List<Part>> {
    val (top, bottom) = input.split("\n\n")
    val workflows = top.lines().map { Workflow.from(it) }.associateBy { it.name }
    val parts = bottom.lines().map { Part.from(it) }
    return Pair(workflows, parts)
}

data class Workflow(val name: String, val rules: List<Rule>) {
    fun test(part: Part): Outcome =
        rules.first { it.test(part) }.outcome

    companion object {
        fun from(s: String) =
            s.split("{").let {
                val name = it[0]
                val rules = it[1].dropLast(1).split(",").map { r ->
                    Rule.from(r)
                }
                Workflow(name, rules)
            }
    }
}

data class Rule(val test: (Part) -> Boolean, val outcome: Outcome) {
    companion object {
        fun from(s: String): Rule {
            val r = Regex("""([x|m|a|s])([>|<])(\d+):(.*)""")
            val match = r.find(s)
            return if (match == null) {
                Rule({ true }, Outcome.from(s))
            } else {
                val (category, comparator, threshold, target) = match.destructured
                val compare = if (comparator == "<") {
                    { n: Int -> n < threshold.toInt() }
                } else {
                    { n: Int -> n > threshold.toInt() }
                }
                val getRating = when (category) {
                    "x" -> Part::x
                    "m" -> Part::m
                    "a" -> Part::a
                    "s" -> Part::s
                    else -> throw Error("?!?!")
                }
                Rule({ compare(getRating(it)) }, Outcome.from(target))
            }
        }
    }
}

sealed class Outcome {
    data class ToWorkflow(val name: String) : Outcome()
    object Reject : Outcome()
    object Accept : Outcome()
    companion object {
        fun from(s: String) =
            when (s) {
                "A" -> Accept
                "R" -> Reject
                else -> ToWorkflow(s)
            }
    }
}

data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    val score = x + m + a + s

    companion object {
        fun from(s: String): Part {
            val r = Regex("""x=(\d+),m=(\d+),a=(\d+),s=(\d+)""")
            val (x, m, a, s) = r.find(s)!!.destructured
            return Part(x = x.toInt(), m = m.toInt(), a = a.toInt(), s = s.toInt())
        }
    }
}
