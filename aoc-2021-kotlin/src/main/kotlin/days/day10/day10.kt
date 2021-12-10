package days.day10

import days.day10.ValidationStatus.Corrupted
import days.day10.ValidationStatus.Incomplete
import days.day10.ValidationStatus.OK
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day10.txt").strings()
    val exampleInput = Reader("day10-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(367059)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(1952146692L)
}

fun part1(input: List<String>) =
    input.map { validate(parse(it)) }
        .filterIsInstance<Corrupted>()
        .sumOf { score(it.closer) }

fun part2(input: List<String>) =
    input.map { validate(parse(it)) }
        .filterIsInstance<Incomplete>()
        .map { scoreIncompleteStack(it.stack) }
        .sorted()
        .let { it[it.size / 2] }

fun parse(input: String) = input.toList().map(Bracket::from)

fun validate(line: List<Bracket>): ValidationStatus {
    tailrec fun go(stack: List<Bracket.Opener>, remaining: List<Bracket>): ValidationStatus {
        if (remaining.isEmpty()) {
            return if (stack.isEmpty()) OK
            else Incomplete(stack)
        }

        return when (val next = remaining.first()) {
            is Bracket.Opener -> go(stack + next, remaining.drop(1))
            is Bracket.Closer -> {
                if (stack.last() == next.opener) go(stack.dropLast(1), remaining.drop(1))
                else Corrupted(next)
            }
        }
    }

    return go(emptyList(), line)
}

fun score(closer: Bracket.Closer) =
    when (closer) {
        Bracket.Closer.Paren -> 3
        Bracket.Closer.Square -> 57
        Bracket.Closer.Curly -> 1197
        Bracket.Closer.Pointy -> 25137
    }

fun scoreIncompleteStack(stack: List<Bracket.Opener>) =
    stack.reversed().fold(0L) { score, opener ->
        when (opener) {
            Bracket.Opener.Paren -> (score * 5) + 1
            Bracket.Opener.Square -> (score * 5) + 2
            Bracket.Opener.Curly -> (score * 5) + 3
            Bracket.Opener.Pointy -> (score * 5) + 4
        }
    }

sealed class Bracket {
    sealed class Opener : Bracket() {
        object Paren : Opener()
        object Square : Opener()
        object Curly : Opener()
        object Pointy : Opener()
    }

    sealed class Closer : Bracket() {
        abstract val opener: Opener

        object Paren : Closer() {
            override val opener = Opener.Paren
        }

        object Square : Closer() {
            override val opener = Opener.Square
        }

        object Curly : Closer() {
            override val opener = Opener.Curly
        }

        object Pointy : Closer() {
            override val opener = Opener.Pointy
        }
    }

    companion object {
        fun from(char: Char): Bracket =
            when (char) {
                '(' -> Opener.Paren
                '[' -> Opener.Square
                '{' -> Opener.Curly
                '<' -> Opener.Pointy
                ')' -> Closer.Paren
                ']' -> Closer.Square
                '}' -> Closer.Curly
                '>' -> Closer.Pointy
                else -> throw Exception("Unrecognised character?!?! $char")
            }
    }
}

sealed class ValidationStatus {
    object OK : ValidationStatus()
    data class Incomplete(val stack: List<Bracket.Opener>) : ValidationStatus()
    data class Corrupted(val closer: Bracket.Closer) : ValidationStatus()
}