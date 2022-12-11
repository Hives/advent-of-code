package days.day11

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day11.txt").string()
    val exampleInput = Reader("day11-example.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(151312L)

    time(message = "Part 2", warmUpIterations = 5, iterations = 5) {
        part2(input)
    }.checkAnswer(51382025916L)
}

fun part1(input: String): Long = doIt(input, 20, part2 = false)
fun part2(input: String): Long = doIt(input, 10_000, part2 = true)

fun doIt(input: String, iterations: Int, part2: Boolean): Long {
    val monkeys = parse(input)
    val inspections = monkeys.indices.associateWith { 0L }.toMutableMap()
    val divisor = monkeys.map { it.testDivisibleBy }.reduce { acc, l -> acc * l }

    tailrec fun turn(monkeys: List<Monkey>, whosTurn: Int): List<Monkey> {
        val currentMonkey = monkeys[whosTurn]

        return if (currentMonkey.itemWorryLevels.isEmpty()) {
            monkeys
        } else {
            inspections[whosTurn] = inspections[whosTurn]!! + 1

            val worryLevel = currentMonkey.worryLevelOperation(currentMonkey.itemWorryLevels.first())
                .let { if (part2) it % divisor else it / 3 }

            val targetMonkey =
                if (worryLevel % currentMonkey.testDivisibleBy == 0L) currentMonkey.trueTarget
                else currentMonkey.falseTarget

            val newMonkeys = monkeys.mapIndexed { index, monkey ->
                when (index) {
                    whosTurn -> monkey.copy(itemWorryLevels = monkey.itemWorryLevels.drop(1))
                    targetMonkey -> monkey.copy(itemWorryLevels = monkey.itemWorryLevels + worryLevel)
                    else -> monkey
                }
            }

            turn(newMonkeys, whosTurn)
        }
    }

    fun round(monkeys: List<Monkey>): List<Monkey> {
        tailrec fun go(monkeys: List<Monkey>, whosTurn: Int): List<Monkey> =
            if (whosTurn >= monkeys.size) monkeys
            else {
                val newMonkeys = turn(monkeys, whosTurn)
                go(newMonkeys, whosTurn + 1)
            }
        return go(monkeys, 0)
    }

    tailrec fun playRounds(monkeys: List<Monkey>, rounds: Int): List<Monkey> =
        if (rounds == 0) monkeys
        else playRounds(round(monkeys), rounds - 1)

    playRounds(monkeys, iterations)

    return inspections.values.sorted().takeLast(2).let { (a, b) -> a * b }
}

fun parse(input: String) =
    input.split("\n\n").map { lines ->
        monkeyRegex.find(lines)!!.destructured
            .let { (monkey, startingItems, operation, testDivisibleBy, trueTarget, falseTarget) ->
                Monkey(
                    itemWorryLevels = startingItems.split(", ").map { it.toLong() },
                    worryLevelOperation = createWorryLevelOperation(operation),
                    testDivisibleBy = testDivisibleBy.toLong(),
                    trueTarget = trueTarget.toInt(),
                    falseTarget = falseTarget.toInt()
                )
            }
    }

fun createWorryLevelOperation(input: String): (Long) -> Long {
    val (operand1, operator, operand2) = input.split(" ")

    fun getGetOperandValue(operand: String): (Long) -> Long =
        if (operand == "old") {
            { old: Long -> old }
        } else {
            { _: Long -> operand.toLong() }
        }

    val getOperandA = getGetOperandValue(operand1)
    val getOperandB = getGetOperandValue(operand2)

    return { n ->
        val a = getOperandA(n)
        val b = getOperandB(n)
        if (operator == "*") a * b else a + b
    }
}

data class Monkey(
    val itemWorryLevels: List<Long>,
    val worryLevelOperation: (Long) -> Long,
    val testDivisibleBy: Long,
    val trueTarget: Int,
    val falseTarget: Int
)

val monkeyRegex = """
    Monkey (\d+):
      Starting items: (.*)
      Operation: new = (.*)
      Test: divisible by (\d+)
        If true: throw to monkey (\d+)
        If false: throw to monkey (\d+)
    """.trimIndent().toRegex()
