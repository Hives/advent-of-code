package days.day25

import lib.time

fun main() {
    time ("part 1 fast using card private key (should be 10548634)") {
        val input = puzzleInput

        val cardPublic = input[0]
        val doorPublic = input[1]

        val cardPrivate = findPrivateKeyWithRecursion(cardPublic)

        transform(cardPrivate, doorPublic)
    }

    time ("part 1 fast using door private key (should be 10548634)") {
        val input = puzzleInput

        val cardPublic = input[0]
        val doorPublic = input[1]

        val doorPrivate = findPrivateKeyWithRecursion(doorPublic)

        transform(doorPrivate, cardPublic)
    }

    time("part 1 (should be 10548634)", 1, 0) {
        val input = puzzleInput

        val cardPublic = input[0]
        val doorPublic = input[1]

        val cardPrivate = findPrivateKeySlowly(cardPublic)

        println(cardPrivate)

        transform(cardPrivate, doorPublic)
    }
}

val exampleInput = listOf(5764801L, 17807724L)
val puzzleInput = listOf(6930903L, 19716708L)

