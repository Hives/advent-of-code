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

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(3247317268284)
}

fun part1(input: List<String>): Long {
    val nodules = nodulize(input, null, null)
    return evaluate(nodules, -1)
}

fun part2(input: List<String>): Long {
    val nodules = nodulize(input, "root", "humn") as Nodule.CompareEquality

    val (target, expression) = when {
        nodules.left is Nodule.Number -> Pair(nodules.left.value, nodules.right)
        nodules.right is Nodule.Number -> Pair(nodules.right.value, nodules.left)
        else -> throw Exception("Something went wrong here")
    }

    tailrec fun binaryPartition(left: Long, right: Long): Long {
        val middle = left + ((right - left) / 2)
        val e = evaluate(expression, middle) - target
        return when {
            e > 0 -> binaryPartition(middle, right)
            e < 0 -> binaryPartition(left, middle)
            else -> middle
        }
    }

    return binaryPartition(0L, Long.MAX_VALUE / 100)
}

fun evaluate(nodule: Nodule, humn: Long): Long =
    when (nodule) {
        Nodule.Humn -> humn
        is Nodule.Number -> nodule.value
        is Nodule.Maths -> nodule.operation.f(evaluate(nodule.left, humn), evaluate(nodule.right, humn))
        is Nodule.CompareEquality -> throw Exception("Don't want to see this here")
    }

fun nodulize(input: List<String>, compareValues: String?, takeInput: String?): Nodule {
    val monkeyMap = input.toMonkeyMap()

    fun go(monkey: String): Nodule {
        val job = monkeyMap[monkey]!!
        return when {
            monkey == compareValues -> {
                val (left, _, right) = job.split(" ")
                Nodule.CompareEquality(go(left), go(right))
            }
            monkey == takeInput -> Nodule.Humn
            job.first().isDigit() -> {
                Nodule.Number(job.toLong())
            }
            else -> {
                val (left, symbol, right) = job.split(" ")
                val leftNodule = go(left)
                val rightNodule = go(right)
                val operation = Operation.from(symbol)
                if (leftNodule is Nodule.Number && rightNodule is Nodule.Number) {
                    Nodule.Number(operation.f(leftNodule.value, rightNodule.value))
                } else {
                    Nodule.Maths(leftNodule, rightNodule, operation)
                }
            }
        }
    }

    return go("root")
}

sealed class Nodule {
    data class CompareEquality(val left: Nodule, val right: Nodule) : Nodule()
    data class Maths(val left: Nodule, val right: Nodule, val operation: Operation) : Nodule()
    data class Number(val value: Long) : Nodule()
    object Humn : Nodule()
}

fun List<String>.toMonkeyMap() =
    associate { it.split(": ").let { (monkey, job) -> monkey to job } }

enum class Operation(val f: (Long, Long) -> Long, val char: Char) {
    ADD(Long::plus, '+'), SUB(Long::minus, '-'), MUL(Long::times, '*'), DIV(Long::div, '/');

    companion object {
        fun from(icon: String) =
            Operation.values().first { "${it.char}" == icon }
    }
}
