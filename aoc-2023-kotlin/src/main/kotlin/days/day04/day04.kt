package days.day04

import lib.Reader
import lib.checkAnswer
import lib.pow
import lib.time

fun main() {
    val input = Reader("/day04/input.txt").strings()
    val exampleInput = Reader("/day04/example-1.txt").strings()

    time(message = "Part 1", warmUpIterations = 500) {
        part1(input)
    }.checkAnswer(20829)

    time(message = "Part 2", warmUpIterations = 500) {
        part2(input)
    }.checkAnswer(12648035)
}

fun part1(input: List<String>): Int =
    input.map(::parse).sumOf { (_, card) ->
        if (card.winnerCount == 0) 0
        else 2.pow(card.winnerCount - 1)
    }

fun part2(input: List<String>): Int {
    val cardMap = input.associate(::parse)
    val cardCounts = cardMap.keys.associateWith { 1 }.toMutableMap()
    (1..cardMap.keys.max()).forEach { cardId ->
        val card = cardMap[cardId]!!
        val newCardIds = List(card.winnerCount) { cardId + it + 1 }
        newCardIds.forEach { newCardId ->
            if (newCardId in cardCounts) {
                cardCounts[newCardId] = cardCounts[newCardId]!! + cardCounts[cardId]!!
            }
        }
    }
    return cardCounts.values.sum()
}

fun parse(input: String): Pair<Int, Card> {
    val (start, end) = input.split(""":\s+""".toRegex())
    val id = """Card\s+(\d+)""".toRegex().find(start)?.destructured?.component1()?.toInt()!!
    val (numbers, winningNumbers) = end.split("""\s+\|\s+""".toRegex()).map {
        it.split("""\s+""".toRegex()).map(String::toInt).toSet()
    }
    return Pair(id, Card(numbers, winningNumbers))
}

data class Card(
    private val numbers: Set<Int>,
    private val winningNumbers: Set<Int>
) {
    val winnerCount = numbers.intersect(winningNumbers).size
}
