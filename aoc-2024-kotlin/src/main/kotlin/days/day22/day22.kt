package days.day22

import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.repeatedlyApply
import lib.time

fun main() {
    val input = Reader("/day22/input.txt").ints()
    val exampleInput1 = Reader("/day22/example-1.txt").ints()
    val exampleInput2 = Reader("/day22/example-2.txt").ints()

    time(message = "Part 1", warmUp = 20, iterations = 20) {
        part1(input)
    }.checkAnswer(16299144133)

    time(message = "Part 2", warmUp = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(1896)
}

fun part1(input: List<Int>): Long =
    input.sumOf {
        it.repeatedlyApply(2000, ::getNextSecret).toLong()
    }

fun part2(input: List<Int>): Int? {
    val allPriceChanges = input.map { getPriceChanges(it, 2000) }

    val priceMaps = allPriceChanges.map { priceChanges ->
        (0.rangeUntil(priceChanges.size - 3)).reversed().associate { index ->
            val sublist = priceChanges.subList(index, index + 4)
            sublist.map { it.diff } to sublist.last().price
        }
    }

    val allDiffSequences = priceMaps.flatMap { it.keys }.distinct()

    return allDiffSequences.maxOfOrNull { diffSeq ->
        priceMaps.sumOf { priceMap ->
            priceMap.getOrDefault(diffSeq, 0)
        }
    }
}

fun getPriceChanges(secret: Int, count: Int): List<PriceDetails> {
    var currentSecret = secret
    val priceChanges = mutableListOf<PriceDetails>()
    repeat(count) {
        val nextSecret = getNextSecret(currentSecret)
        val price = nextSecret.mod(10)
        val diff = price - currentSecret.mod(10)
        priceChanges.add(PriceDetails(price = price, diff = diff))
        currentSecret = nextSecret
    }
    return priceChanges
}

data class PriceDetails(val price: Int, val diff: Int)

fun getNextSecret(n: Int): Int {
    var secretNumber = n
    val a = 64 * secretNumber
    secretNumber = mix(a, secretNumber)
    secretNumber = prune(secretNumber)
    val b = secretNumber / 32
    secretNumber = mix(b, secretNumber)
    secretNumber = prune(secretNumber)
    val c = secretNumber * 2048
    secretNumber = mix(c, secretNumber)
    secretNumber = prune(secretNumber)
    return secretNumber
}

fun mix(a: Int, b: Int): Int = a.xor(b)

fun prune(n: Int): Int = n.mod(16777216)
