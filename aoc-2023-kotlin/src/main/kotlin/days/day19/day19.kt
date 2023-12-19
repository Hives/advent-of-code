package days.day19

import days.day19.Inequality.GT
import days.day19.Inequality.LT
import days.day19.Outcome.Accept
import days.day19.Outcome.Reject
import days.day19.Outcome.ToWorkflow
import days.day19.Rule.NoTest
import days.day19.Rule.Test
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day19/input.txt").string()
    val exampleInput = Reader("/day19/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(386787)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(131029523269531L)
}

fun part1(input: String): Int {
    val (workflows, parts) = parse(input)
    val acceptedParts = parts.filter { part ->
        var currentWorkflow = workflows["in"]!!
        while (true) {
            val outcome = currentWorkflow.rules.first { rule ->
                when (rule) {
                    is Test -> {
                        val rating = when (rule.category) {
                            Category.X -> part.x
                            Category.M -> part.m
                            Category.A -> part.a
                            Category.S -> part.s
                        }
                        when (rule.comparator) {
                            GT -> rating > rule.threshold
                            LT -> rating < rule.threshold
                        }
                    }

                    is NoTest -> true
                }
            }.outcome
            when (outcome) {
                Accept -> return@filter true
                Reject -> return@filter false
                is ToWorkflow -> currentWorkflow = workflows[outcome.name]!!
            }
        }
        throw Error("omgwtfbbq")
    }
    return acceptedParts.sumOf(Part::score)
}

fun part2(input: String): Long {
    val (workflows, _) = parse(input)

    val initial = Category.values().associateWith { Pair(1L, 4_000L) }

    val unevaluatedRangeMaps: MutableList<Pair<RangeMap, Workflow>> =
        mutableListOf(Pair(initial, workflows.getValue("in")))
    var acceptedCount = 0L

    while (unevaluatedRangeMaps.isNotEmpty()) {
        val (rangeMap, workflow) = unevaluatedRangeMaps.removeLast()

        var currentRangeMap = rangeMap

        for (rule in workflow.rules) {
            when (rule) {
                is NoTest -> {
                    when (val outcome = rule.outcome) {
                        is Accept -> acceptedCount += currentRangeMap.distinctCombos()
                        is ToWorkflow -> unevaluatedRangeMaps.add(
                            Pair(
                                currentRangeMap,
                                workflows.getValue(outcome.name)
                            )
                        )

                        Reject -> {}
                    }
                    break
                }

                is Test -> {
                    val ratingRange = currentRangeMap[rule.category]!!
                    val (passRange, failRange) = ratingRange.split(rule.comparator, rule.threshold)
                    if (passRange != null) {
                        val passRangeMap = currentRangeMap.replace(rule.category, passRange)
                        when (val outcome = rule.outcome) {
                            Accept -> acceptedCount += passRangeMap.distinctCombos()
                            is ToWorkflow -> unevaluatedRangeMaps.add(
                                Pair(
                                    passRangeMap,
                                    workflows.getValue(outcome.name)
                                )
                            )

                            Reject -> {}
                        }
                    }
                    if (failRange != null) {
                        val failRangeMap = currentRangeMap.replace(rule.category, failRange)
                        currentRangeMap = failRangeMap
                    } else {
                        break
                    }
                }
            }
        }
    }

    return acceptedCount
}

fun Range.split(comparator: Inequality, threshold: Int): Pair<Range?, Range?> =
    when (comparator) {
        GT -> {
            when {
                threshold > second -> Pair(null, this)
                threshold < first -> Pair(this, null)
                else -> {
                    val pass = Pair(threshold.toLong() + 1, second)
                    val fail = Pair(first, threshold.toLong())
                    Pair(pass, fail)
                }
            }
        }

        LT -> {
            when {
                threshold > second -> Pair(this, null)
                threshold < first -> Pair(null, this)
                else -> {
                    val pass = Pair(first, threshold.toLong() - 1)
                    val fail = Pair(threshold.toLong(), second)
                    Pair(pass, fail)
                }
            }
        }
    }

typealias Range = Pair<Long, Long>
typealias RangeMap = Map<Category, Range>

fun RangeMap.distinctCombos() =
    values.map { it.second - it.first + 1 }.fold(1, Long::times)

fun RangeMap.replace(otherKey: Category, otherValue: Range): RangeMap =
    keys.associate { key ->
        if (key == otherKey) key to otherValue
        else key to this[key]!!
    }

fun parse(input: String): Pair<Map<String, Workflow>, List<Part>> {
    val (top, bottom) = input.split("\n\n")
    val workflows = top.lines().map { Workflow.from(it) }.associateBy { it.name }
    val parts = bottom.lines().map { Part.from(it) }
    return Pair(workflows, parts)
}

data class Workflow(val name: String, val rules: List<Rule>) {
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

sealed class Rule(open val outcome: Outcome) {
    data class Test(
        val category: Category,
        val comparator: Inequality,
        val threshold: Int,
        override val outcome: Outcome
    ) : Rule(outcome)

    data class NoTest(override val outcome: Outcome) : Rule(outcome)

    companion object {
        fun from(s: String): Rule {
            val r = Regex("""([xmas])([><])(\d+):(.*)""")
            val match = r.find(s)
            return if (match == null) {
                NoTest(Outcome.from(s))
            } else {
                val (category, comparator, threshold, target) = match.destructured
                Test(
                    category = Category.valueOf(category.uppercase()),
                    comparator = if (comparator == ">") GT else LT,
                    threshold = threshold.toInt(),
                    outcome = Outcome.from(target)
                )
            }
        }
    }
}

enum class Inequality { GT, LT }

enum class Category { X, M, A, S }

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
