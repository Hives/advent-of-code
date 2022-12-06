package days.day06

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day06.txt").string()
    val exampleInput = Reader("day06-example.txt").string()

    time(message = "Part 1 (for loop)", warmUpIterations = 500, iterations = 1000) {
        part1(input)
    }.checkAnswer(1531)

    time(message = "Part 2 (for loop)", warmUpIterations = 500, iterations = 1000) {
        part2(input)
    }.checkAnswer(2518)

    time(message = "Part 1 (windowed)", warmUpIterations = 500, iterations = 1000) {
        part12(input)
    }.checkAnswer(1531)

    time(message = "Part 2 (windowed)", warmUpIterations = 500, iterations = 1000) {
        part22(input)
    }.checkAnswer(2518)
}

fun part1(input: String) = input.findFirstDistinctCharacterSequence(4)
fun part2(input: String) = input.findFirstDistinctCharacterSequence(14)

fun part12(input: String) = input.findFirstDistinctCharacterSequence2(4)
fun part22(input: String) = input.findFirstDistinctCharacterSequence2(14)

fun String.findFirstDistinctCharacterSequence(seqLength: Int) =
    (seqLength..(this.length)).first { i ->
        this.substring(i - seqLength, i).toSet().size == seqLength
    }

fun String.findFirstDistinctCharacterSequence2(seqLength: Int) =
    windowed(seqLength).indexOfFirst { it.toSet().size == seqLength } + seqLength
