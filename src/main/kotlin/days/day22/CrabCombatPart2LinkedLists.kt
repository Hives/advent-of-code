package days.day22

import days.day22.Player.PLAYER_A
import days.day22.Player.PLAYER_B
import java.util.*
import lib.log

enum class Player(val index: Int) {
    PLAYER_A(0),
    PLAYER_B(1)
}

class Part2StandardLists(private val hands: Pair<List<Int>, List<Int>>) {
    var gameCount = 0
    var roundCount = 0
    var maxRound = 0
    var score = -1

    fun run(): Part2StandardLists {
        score = startGame(hands.first, hands.second).second.score()
        return this
    }

    private fun startGame(
        handA: List<Int>,
        handB: List<Int>,
    ): Pair<Player, List<Int>> {
        gameCount++
        val thisGame = gameCount

        log("=== Game $thisGame ===")
        val results = play(handA, handB, emptyList(), emptyList(), thisGame, 1)
        log("The winner of game $thisGame is player ${results.first.index + 1}!")
        return results
    }

    private tailrec fun play(
        handA: List<Int>,
        handB: List<Int>,
//        history: List<Pair<List<Int>, List<Int>>>,
        historyA: List<List<Int>>,
        historyB: List<List<Int>>,
        gameLevel: Int,
        round: Int
    ): Pair<Player, List<Int>> {
        if (handA.isEmpty()) return Pair(PLAYER_B, handB)
        if (handB.isEmpty()) return Pair(PLAYER_A, handA)
        if (historyA.contains(handA) && historyB.contains(handB)) return Pair(PLAYER_A, handA)

        val (newHandA, newHandB) = oneRound(handA, handB, gameLevel, round)

        return play(newHandA, newHandB, historyA + listOf(handA), historyB + listOf(handB), gameLevel, round + 1)
    }

    private fun oneRound(handA: List<Int>, handB: List<Int>, gameLevel: Int, round: Int): Pair<List<Int>, List<Int>> {
        roundCount++
        if (round > maxRound) maxRound = round

//        if (roundCount == 1_000) exitProcess(0)

        log("\n-- Round $round (Game $gameLevel) --")
        log("Player 1's deck: ${handA.joinToString(", ")}")
        log("Player 2's deck: ${handB.joinToString(", ")}")

        val (a, newHandA) = handA.takeQ()
        val (b, newHandB) = handB.takeQ()

        log("Player 1 plays: $a")
        log("Player 2 plays: $b")

        val winner = if (newHandA.size >= a && newHandB.size >= b) {
            log("Playing a sub-game to determine the winner...\n")
            startGame(newHandA.take(a), newHandB.take(b)).first
                .also { log("\n...anyway, back to game $gameLevel.") }
        } else {
            if (a > b) PLAYER_A else PLAYER_B
        }

        log("Player ${winner.index + 1} wins round ${round} of game ${gameLevel}!")

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

object Part2LinkedLists {
    fun run(hands: Pair<List<Int>, List<Int>>): Int {
        val handA = LinkedList(hands.first)
        val handB = LinkedList(hands.second)
        val winner = play(handA, handB, emptyList())
        return listOf(handA, handB)[winner.index].score()
    }

    private tailrec fun play(
        handA: LinkedList<Int>,
        handB: LinkedList<Int>,
        history: List<Pair<List<Int>, List<Int>>>
    ): Player {
        if (handA.isEmpty()) return PLAYER_B
        if (handB.isEmpty()) return PLAYER_A

        val handAFrozen = handA.toList()
        val handBFrozen = handB.toList()

        if (history.contains(Pair(handAFrozen, handBFrozen))) return PLAYER_A

        oneRound(handA, handB)

        return play(handA, handB, history + Pair(handAFrozen, handBFrozen))
    }

    private fun oneRound(handA: LinkedList<Int>, handB: LinkedList<Int>) {
        val a = handA.poll()
        val b = handB.poll()

        val winner = if (handA.size >= a && handB.size >= b) {
            play(
                handA = LinkedList(handA.toList().take(a)),
                handB = LinkedList(handB.toList().take(b)),
                history = emptyList()
            )
        } else {
            if (a > b) PLAYER_A else PLAYER_B
        }

        listOf(
            {
                handA.add(a)
                handA.add(b)
            },
            {
                handB.add(b)
                handB.add(a)
            }
        )[winner.index]()
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
