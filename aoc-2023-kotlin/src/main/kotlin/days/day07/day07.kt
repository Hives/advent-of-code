package days.day07

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day07/input.txt").strings()
    val exampleInput = Reader("/day07/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(246795406)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(249356515)
}

fun part1(input: List<String>) =
    input.map(Hand::parse1).totalWinnings()

fun part2(input: List<String>) =
    input.map(Hand::parse2).totalWinnings()

fun List<Hand>.totalWinnings(): Int =
    sorted().mapIndexed { index, hand ->
        val rank = index + 1
        rank * hand.bid
    }.sum()

data class Hand(val cards: List<Card>, val bid: Int) : Comparable<Hand> {
    private val type: Type
        get() {
            val groupingSizes = cards.groupBy { it.face }.values.map { it.size }.sortedDescending()
            return when {
                (groupingSizes == listOf(5)) -> Type.FIVE
                (groupingSizes == listOf(4, 1)) -> Type.FOUR
                (groupingSizes == listOf(3, 2)) -> Type.FULL_HOUSE
                (groupingSizes == listOf(3, 1, 1)) -> Type.THREE
                (groupingSizes == listOf(2, 2, 1)) -> Type.TWO_PAIR
                (groupingSizes == listOf(2, 1, 1, 1)) -> Type.ONE_PAIR
                else -> Type.HIGH_CARD
            }
        }

    override fun compareTo(other: Hand): Int {
        return if (this.type != other.type) this.type.rank compareTo other.type.rank
        else {
            this.cards.zip(other.cards)
                .firstOrNull { it.first.rank != it.second.rank }
                ?.let { it.first.rank compareTo it.second.rank }
                ?: 0
        }
    }

    companion object {
        fun parse1(input: String): Hand {
            val (start, end) = input.split(" ")
            return Hand(
                cards = start.map { Card.from(it) },
                bid = end.toInt()
            )
        }

        fun parse2(input: String): Hand {
            val hand = parse1(input.replace('J', '!'))
            val cards = hand.cards
            val mostPopularNonJokerFace =
                cards.groupBy { it.face }
                    .filter { it.key != '!' }
                    .maxByOrNull { it.value.size }?.key ?: 'A'

            return hand.copy(
                cards = cards.map {
                    if (it.face == '!') it.copy(face = mostPopularNonJokerFace)
                    else it
                }
            )
        }
    }

    enum class Type(val rank: Int) {
        FIVE(6),
        FOUR(5),
        FULL_HOUSE(4),
        THREE(3),
        TWO_PAIR(2),
        ONE_PAIR(1),
        HIGH_CARD(0)
    }
}

data class Card(val face: Char, val rank: Int) {
    companion object {
        fun from(c: Char) =
            when (c) {
                '!' -> Card(c, 0)
                '2' -> Card(c, 2)
                '3' -> Card(c, 3)
                '4' -> Card(c, 4)
                '5' -> Card(c, 5)
                '6' -> Card(c, 6)
                '7' -> Card(c, 7)
                '8' -> Card(c, 8)
                '9' -> Card(c, 9)
                'T' -> Card(c, 10)
                'J' -> Card(c, 11)
                'Q' -> Card(c, 12)
                'K' -> Card(c, 13)
                'A' -> Card(c, 14)
                else -> throw Error("Bad face char: $c")
            }
    }
}
