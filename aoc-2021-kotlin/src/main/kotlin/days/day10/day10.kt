package days.day10

import lib.Reader

fun main() {
    val input = Reader("day10.txt").strings()
    val exampleInput = Reader("day10-example.txt").strings()

    input.map { validate(it.toList()) }
        .filterIsInstance<ValidationStatus.Corrupted>()
        .sumOf { score(it.char) }
        .also { println(it) }

    input.map { validate(it.toList()) }
        .filterIsInstance<ValidationStatus.Incomplete>()
        .map { completeStack(it.stack).let(::scoreCompletion) }
        .sorted()
        .let { it[it.size/2] }
        .also { println(it) }
}

fun validate(line: List<Char>): ValidationStatus {
    tailrec fun go(openerStack: List<Char>, remaining: List<Char>): ValidationStatus {
        if (remaining.isEmpty()) {
            return if (openerStack.isEmpty()) ValidationStatus.OK
            else ValidationStatus.Incomplete(openerStack)
        }

        val next = remaining.first()
        when (next) {
            '(' -> return go(openerStack + next, remaining.drop(1))
            '[' -> return go(openerStack + next, remaining.drop(1))
            '{' -> return go(openerStack + next, remaining.drop(1))
            '<' -> return go(openerStack + next, remaining.drop(1))
            ')' -> {
                return if (openerStack.last() == '(') go(openerStack.subList(0, (openerStack.size - 1)),
                    remaining.drop(1))
                else ValidationStatus.Corrupted(next)
            }
            ']' -> {
                return if (openerStack.last() == '[') go(openerStack.subList(0, (openerStack.size - 1)),
                    remaining.drop(1))
                else ValidationStatus.Corrupted(next)
            }
            '}' -> {
                return if (openerStack.last() == '{') go(openerStack.subList(0, (openerStack.size - 1)),
                    remaining.drop(1))
                else ValidationStatus.Corrupted(next)
            }
            '>' -> {
                return if (openerStack.last() == '<') go(openerStack.subList(0, (openerStack.size - 1)),
                    remaining.drop(1))
                else ValidationStatus.Corrupted(next)
            }
            else -> throw Exception("Unrecognised character?!?! $next")
        }
    }

    return go(emptyList(), line)
}

fun completeStack(openerStack: List<Char>) =
    openerStack.reversed().fold(emptyList<Char>()) { acc, char ->
        when (char) {
            '(' -> acc + ')'
            '[' -> acc + ']'
            '{' -> acc + '}'
            '<' -> acc + '>'
            else -> throw Exception("Unrecognised character?!?! $char")
        }
    }

fun score(char: Char) =
    when (char) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> throw Exception("Unrecognised character?!?! $char")
    }

fun scoreCompletion(chars: List<Char>) =
    chars.fold(0L) { score, char ->
        when (char) {
            ')' -> (score * 5) + 1L
            ']' -> (score * 5) + 2L
            '}' -> (score * 5) + 3L
            '>' -> (score * 5) + 4L
            else -> throw Exception("Unrecognised character?!?! $char")
        }
    }

sealed class ValidationStatus {
    object OK : ValidationStatus()
    data class Incomplete(val stack: List<Char>) : ValidationStatus()
    data class Corrupted(val char: Char) : ValidationStatus()
}