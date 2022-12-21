package days.day21

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day21.txt").strings()
    val exampleInput = Reader("day21-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(169525884255464L)

    part2(input).checkAnswer(3247317268284)
}

fun part1(input: List<String>): Long {
    val jobMap = parse(input)
    return Evaluator1(jobMap).findRootValue()
}

fun part2(input: List<String>): Long {
    val jobMap = parse2(input)

    val evaluator = Evaluator2(jobMap)
    val evaluated = evaluator.evaluate("root") as Nodule.CompareEquality
    val nodules = evaluated.left
    val target = (evaluated.right as Nodule.Number).value

    fun evaluate(nodule: Nodule, humn: Long): Long =
        when(nodule) {
            is Nodule.Number -> nodule.value
            Nodule.Humn -> humn
            is Nodule.Maths -> nodule.operation.f(evaluate(nodule.left, humn), evaluate(nodule.right, humn))
            is Nodule.CompareEquality -> TODO()
        }

    // found by manual investigation
    generateSequence(3247317268200L) { it + 1L }.take(1000).forEach { current ->
        val answer = evaluate(nodules, current) - target
        if (answer == 0L) return current
    }

    throw Exception("No answer")

}

class Evaluator1(private val jobMap: Map<String, Job>) {
    fun findRootValue() = evaluate("root")

    private val evaluate = DeepRecursiveFunction { monkey ->
        when (val job = jobMap[monkey]!!) {
            is Number -> job.value
            is Math -> job.operation.f(
                callRecursive(job.left),
                callRecursive(job.right)
            )
        }
    }
}

class Evaluator2(private val jobMap: Map<String, Job2>) {
    val humnChain = findHumnRootChain()

//    fun passesEqualityTest(testValue: Long) = evaluate(Pair("root", testValue)) == 1L

    private fun findHumnRootChain(): List<String> {
        tailrec fun go(monkeyChain: List<String>): List<String> {
            return if (monkeyChain.last() == "root") monkeyChain
            else {
                val next = findMonkeyReferringTo(monkeyChain.last())
                go(monkeyChain + next)
            }
        }
        return go(listOf("humn"))
    }

    private fun findMonkeyReferringTo(requestedMonkey: String): String {
        val (monkey) = jobMap.toList().single { (_, job) ->
            (job is Math && (job.left == requestedMonkey || job.right == requestedMonkey))
                    || (job is CheckEquality && (job.left == requestedMonkey || job.right == requestedMonkey))
        }
        return monkey
    }

    val evaluate = DeepRecursiveFunction<String, Nodule> { monkey ->
        when (val job = jobMap[monkey]!!) {
            TakeInput -> Nodule.Humn
            is CheckEquality -> Nodule.CompareEquality(callRecursive(job.left), callRecursive(job.right))
            is Math -> {
                val left = callRecursive(job.left)
                val right = callRecursive(job.right)
                when {
                    left is Nodule.Number && right is Nodule.Number -> Nodule.Number(
                        job.operation.f(
                            left.value,
                            right.value
                        )
                    )

                    else -> Nodule.Maths(left, right, job.operation)
                }
            }

            is Number -> Nodule.Number(job.value)
        }
    }
}

sealed class Nodule {
    data class CompareEquality(val left: Nodule, val right: Nodule) : Nodule() {
        override fun stringify(): String {
            TODO("Not yet implemented")
        }
    }

    data class Maths(val left: Nodule, val right: Nodule, val operation: Operation) : Nodule() {
        override fun stringify(): String {
            fun stringifySide(side: Nodule) =
                when (side) {
                    is Maths -> "(${side.stringify()})"
                    else -> side.stringify()
                }
            return "${stringifySide(left)} ${operation.char} ${stringifySide(right)}"
        }
    }

    data class Number(val value: Long) : Nodule() {
        override fun stringify() = value.toString()
    }

    object Humn : Nodule() {
        override fun stringify() = "HUMN"
    }

    abstract fun stringify(): String
}

fun parse(input: List<String>): Map<String, Job> =
    input.associate {
        it.split(": ").let { (monkey, job) ->
            if (job.first().isDigit()) Pair(monkey, Number(job.toLong()))
            else {
                job.split(" ").let { (monkey1, operation, monkey2) ->
                    Pair(monkey, Math(monkey1, monkey2, Operation.from(operation)))
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
                    job.split(" ").let { (monkey1, operation, monkey2) ->
                        Pair(monkey, Math(monkey1, monkey2, Operation.from(operation)))
                    }
                }
            }
        }
    }

enum class Operation(val f: (Long, Long) -> Long, val char: Char) {
    ADD(Long::plus, '+'), SUB(Long::minus, '-'), MUL(Long::times, '*'), DIV(Long::div, '/');

    companion object {
        fun from(icon: String) =
            Operation.values().first { "${it.char}" == icon }
    }
}

sealed interface Job
sealed interface Job2

data class Number(val value: Long) : Job, Job2
data class Math(val left: String, val right: String, val operation: Operation) : Job, Job2
data class CheckEquality(val left: String, val right: String) : Job2
object TakeInput : Job2

