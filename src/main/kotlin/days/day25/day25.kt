package days.day25

import lib.time

fun main() {
    time("part 1 (should be 10548634)", 1, 0) {
        val input = puzzleInput

        val cardPublic = input[0]
        val doorPublic = input[1]

        val cardPrivate = findPrivateKey(cardPublic)
        val doorPrivate = findPrivateKey(doorPublic)

        listOf(
            Pair(cardPrivate, doorPublic),
            Pair(doorPrivate, cardPublic)
        ).map { transform(it.first, it.second) }
    }
}

val exampleInput = listOf(5764801L, 17807724L)
val puzzleInput = listOf(6930903L, 19716708L)

