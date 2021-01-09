package days.day22

import days.day22.Player.PLAYER_A
import days.day22.Player.PLAYER_B
import java.util.*

object Part2LinkedLists {
    fun run(hands: Pair<List<Int>, List<Int>>) = Game(hands.first, hands.second).play().second.score()

    class Game(handAList: List<Int>, handBList: List<Int>) {
        private val handA = LinkedList(handAList)
        private val handB = LinkedList(handBList)
        private val history = mutableSetOf<Pair<List<Int>, List<Int>>>()

        tailrec fun play(): Pair<Player, List<Int>> {
            val handACopy = handA.toList()
            val handBCopy = handB.toList()

            if (handA.isEmpty()) return Pair(PLAYER_B, handBCopy)
            if (handB.isEmpty()) return Pair(PLAYER_A, handACopy)

            val hands = Pair(handACopy, handBCopy)
            if (hands in history) return Pair(PLAYER_A, handACopy)

            history.add(hands)

            oneRound()

            return play()
        }

        private fun oneRound() {
            val a = handA.poll()
            val b = handB.poll()

            val winner = if (handA.size >= a && handB.size >= b) {
                Game(handA.toList().take(a), handB.toList().take(b)).play().first
            } else {
                if (a > b) PLAYER_A else PLAYER_B
            }

            if (winner == PLAYER_A) {
                handA.add(a)
                handA.add(b)
            } else {
                handB.add(b)
                handB.add(a)
            }
        }
    }
}

object Part2StandardLists  {
    fun run(hands: Pair<List<Int>, List<Int>>) = Game().play(hands.first, hands.second).second.score()

    class Game {
        private val history = mutableSetOf<Pair<List<Int>, List<Int>>>()

        tailrec fun play(
            handA: List<Int>,
            handB: List<Int>,
        ): Pair<Player, List<Int>> {
            if (handA.isEmpty()) return Pair(PLAYER_B, handB)
            if (handB.isEmpty()) return Pair(PLAYER_A, handA)

            val hands = Pair(handA, handB)
            if (hands in history) return Pair(PLAYER_A, handA)

            history.add(hands)
            val (newHandA, newHandB) = oneRound(handA, handB)

            return play(newHandA, newHandB)
        }

        private fun oneRound(handA: List<Int>, handB: List<Int>): Pair<List<Int>, List<Int>> {
            val (a, newHandA) = handA.takeQ()
            val (b, newHandB) = handB.takeQ()

            val winner = if (newHandA.size >= a && newHandB.size >= b) {
                Game().play(newHandA.take(a), newHandB.take(b)).first
            } else {
                if (a > b) PLAYER_A else PLAYER_B
            }

            return if (winner == PLAYER_A) {
                Pair(
                    newHandA.addQ(a).addQ(b),
                    newHandB
                )
            } else {
                Pair(
                    newHandA,
                    newHandB.addQ(b).addQ(a),
                )
            }
        }
    }
}

fun part1LinkedLists(hands: Pair<List<Int>, List<Int>>): Int {
    val handA = LinkedList(hands.first)
    val handB = LinkedList(hands.second)

    while (!handA.isEmpty() && !handB.isEmpty()) {
        val a = handA.poll()
        val b = handB.poll()
        if (a > b) {
            handA.add(a)
            handA.add(b)
        } else {
            handB.add(b)
            handB.add(a)
        }
    }

    return if (handA.isEmpty()) handB.score() else handA.score()
}

fun part1StandardLists(hands: Pair<List<Int>, List<Int>>): Int {
    fun oneRound(handA: List<Int>, handB: List<Int>): Pair<List<Int>, List<Int>> {
        val (a, handA2) = handA.takeQ()
        val (b, handB2) = handB.takeQ()

        return if (a > b) {
            Pair(
                handA2.addQ(a).addQ(b),
                handB2
            )
        } else {
            Pair(
                handA2,
                handB2.addQ(b).addQ(a),
            )
        }
    }

    tailrec fun play(handA: List<Int>, handB: List<Int>): List<Int> {
        if (handA.isEmpty()) return handB
        if (handB.isEmpty()) return handA

        val (newHandA, newHandB) = oneRound(handA, handB)

        return play(newHandA, newHandB)
    }

    return play(hands.first, hands.second).score()
}

enum class Player { PLAYER_A, PLAYER_B }

fun <T> List<T>.takeQ(): Pair<T, List<T>> {
    require(this.isNotEmpty()) { "Can't take from empty list." }
    return Pair(this.first(), this.drop(1))
}

fun <T> List<T>.addQ(new: T): List<T> = this + new

fun Iterable<Int>.score() =
    this.toList().reversed().mapIndexed { index, i -> (index + 1) * i }.reduce { a, b -> a + b }

fun parseInput(input: String): Pair<List<Int>, List<Int>> =
    input.trim().split("\n\n")
        .map { hand ->
            hand.lines().drop(1).map { it.toInt() }
        }.let { Pair(it[0], it[1]) }

fun hash(handA: List<Int>, handB: List<Int>) = hash(handA) + "+" + hash(handB)
fun hash(hand: List<Int>) = hand.joinToString { it.toString().padStart(2, '0') }
