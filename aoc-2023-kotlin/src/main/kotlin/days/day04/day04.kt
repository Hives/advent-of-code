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
    input.map(::parse).sumOf { card ->
        if (card.winnerCount == 0) 0
        else 2.pow(card.winnerCount - 1)
    }

fun part2(input: List<String>): Int {
    val cards = input.map(::parse)
    val cardCounts = List(cards.size) { 1 }.toMutableList()
    cards.forEachIndexed { index, card ->
        val newCardIds = List(card.winnerCount) { index + it + 1 }
        newCardIds.forEach { newCardId ->
            if (newCardId < cardCounts.size) {
                cardCounts[newCardId] += cardCounts[index]
            }
        }
    }
    return cardCounts.sum()
}

fun parse(input: String): Card {
    val (numbers, winningNumbers) = input.split(""":\s+""".toRegex())[1].split("""\s+\|\s+""".toRegex()).map {
        it.split("""\s+""".toRegex()).map(String::toInt).toSet()
    }
    return Card(numbers, winningNumbers)
}

data class Card(
    private val numbers: Set<Int>,
    private val winningNumbers: Set<Int>
) {
    val winnerCount = numbers.intersect(winningNumbers).size
}
