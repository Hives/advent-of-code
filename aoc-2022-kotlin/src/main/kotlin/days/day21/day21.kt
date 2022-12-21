package days.day21

import lib.Reader

fun main() {
    val input = Reader("day21.txt").strings()
    val exampleInput = Reader("day21-example.txt").strings()

//    part1(input).checkAnswer(169525884255464L)
    println(part2(exampleInput))
}

fun part1(input: List<String>): Long {
    val jobMap = parse(input)
    return Evaluator1(jobMap).findRootValue()
}

fun part2(input: List<String>): Int {
    val jobMap = parse2(input)

    return (1..Int.MAX_VALUE).first { n ->
        if (n % 100_000 == 0) println(n)
        Evaluator2(jobMap, n.toLong()).passesEqualityTest()
    }
}

class Evaluator1(private val jobMap: Map<String, Job>) {
    fun findRootValue() = evaluate("root")

    private val evaluate = DeepRecursiveFunction { monkey ->
        when (val job = jobMap[monkey]!!) {
            is Number -> job.value
            is Math -> job.operation(
                callRecursive(job.monkey1),
                callRecursive(job.monkey2)
            )
        }
    }
}

class Evaluator2(private val jobMap: Map<String, Job2>, private val humanValue: Long) {
    fun passesEqualityTest() = evaluate("root") == 1L

    private val evaluate = DeepRecursiveFunction { monkey ->
        when (val job = jobMap[monkey]!!) {
            is Number -> job.value
            is Math -> {
                job.operation(
                    this.callRecursive(job.monkey1),
                    this.callRecursive(job.monkey2)
                )
            }

            is CheckEquality -> {
                if (
                    callRecursive(job.monkey1) == callRecursive(job.monkey2)
                ) 1L else 0L
            }

            TakeInput -> humanValue
        }
    }
}


fun parse(input: List<String>): Map<String, Job> =
    input.associate {
        it.split(": ").let { (monkey, job) ->
            if (job.first().isDigit()) Pair(monkey, Number(job.toLong()))
            else {
                job.split(" ").let { (monkey1, operator, monkey2) ->
                    val operation: (Long, Long) -> Long = when (operator) {
                        "+" -> Long::plus
                        "-" -> Long::minus
                        "*" -> Long::times
                        "/" -> Long::div
                        else -> throw Exception("Unknown operator: $operator")
                    }
                    Pair(monkey, Math(monkey1, monkey2, operation))
                }
            }
        }
    }

fun parse2(input: List<String>) =
    input.associate {
        it.split(": ").let { (monkey, job) ->
            when {
                monkey == "root" -> {
                    job.split(" ").let { (monkey1, _, monkey2) ->
                        Pair(monkey, CheckEquality(monkey1, monkey2))
                    }
                }

                monkey == "humn" -> {
                    Pair(monkey, TakeInput)
                }

                job.first().isDigit() -> {
                    Pair(monkey, Number(job.toLong()))
                }

                else -> {
                    job.split(" ").let { (monkey1, operator, monkey2) ->
                        val operation: (Long, Long) -> Long = when (operator) {
                            "+" -> Long::plus
                            "-" -> Long::minus
                            "*" -> Long::times
                            "/" -> Long::div
                            else -> throw Exception("Unknown operator: $operator")
                        }
                        Pair(monkey, Math(monkey1, monkey2, operation))
                    }
                }
            }
        }
    }

sealed interface Job
sealed interface Job2

data class Number(val value: Long) : Job, Job2
data class Math(val monkey1: String, val monkey2: String, val operation: (Long, Long) -> Long) : Job, Job2
data class CheckEquality(val monkey1: String, val monkey2: String) : Job2
object TakeInput : Job2

