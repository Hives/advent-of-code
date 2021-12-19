package days.day18

import days.day18.Token.Bracket.Close
import days.day18.Token.Bracket.Open
import days.day18.Token.Value
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day18.txt").strings()
    val exampleInput = Reader("day18-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(3763)

    time(message = "Part 2", iterations = 10, warmUpIterations = 10) {
        part2(input)
    }.checkAnswer(4664)
}

fun part1(input: List<String>): Int = input.map(::parse).reduce(::add).let(::magnitude)

fun part2(input: List<String>): Int? {
    val numbers = input.map(::parse)

    val pairs = numbers.flatMap { left ->
        numbers.mapNotNull { right ->
            if (left == right) null
            else Pair(left, right)
        }
    }

    return pairs.maxOfOrNull { (left, right) -> magnitude(add(left, right)) }
}

fun magnitude(tokens: List<Token>): Int {
    if (tokens.size == 1) return (tokens.single() as Value).value
    val (left, right) = constituents(tokens)
    return (3 * magnitude(left)) + (2 * magnitude(right))
}

fun constituents(tokens: List<Token>): Pair<List<Token>, List<Token>> {
    val stripped = tokens.drop(1).dropLast(1)

    var index = -1
    var openCount = 0

    while (index < stripped.size) {
        index += 1
        if (stripped[index] == Open) openCount += 1
        if (stripped[index] == Close) openCount -= 1
        if (openCount == 0) break
    }

    val left = stripped.subList(0, index + 1)
    val right = stripped.subList(index + 1, stripped.size)

    return Pair(left, right)
}

fun add(left: List<Token>, right: List<Token>) =
    reduceExpression((listOf(Open) + left + right + Close))

tailrec fun reduceExpression(tokens: List<Token>): List<Token> {
    val exploded = explode(tokens)
    if (exploded != tokens) return reduceExpression(exploded)
    val splitted = split(tokens)
    if (splitted != tokens) return reduceExpression(splitted)
    return tokens
}

fun explode(tokens: List<Token>): List<Token> {
    var index = 0
    var openCount = 0
    while (index < tokens.size) {
        if (tokens[index] == Open) openCount += 1
        if (tokens[index] == Close) openCount -= 1
        if (openCount == 5) {
            val mutableTokens = tokens.toMutableList()

            val left = tokens[index + 1] as Value
            val right = tokens[index + 2] as Value

            var indexL = index
            while (true) {
                if (indexL < 0) break
                if (mutableTokens[indexL] is Value) break
                indexL -= 1
            }
            if (indexL >= 0) {
                val oldValue = mutableTokens[indexL] as Value
                mutableTokens[indexL] = oldValue + left
            }

            var indexR = index + 3
            while (true) {
                if (indexR >= mutableTokens.size) break
                if (mutableTokens[indexR] is Value) break
                indexR += 1
            }
            if (indexR < mutableTokens.size) {
                val oldValue = mutableTokens[indexR] as Value
                mutableTokens[indexR] = oldValue + right
            }

            return mutableTokens.subList(0, index) + Value(0) + mutableTokens.subList(index + 4, mutableTokens.size)
        }
        index++
    }
    return tokens
}

fun split(tokens: List<Token>): List<Token> {
    var index = 0
    while (index < tokens.size) {
        val token = tokens[index]
        if (token is Value && token.value >= 10) {
            val (left, right) = token.split()
            return tokens.subList(0, index) + listOf(Open, left, right, Close) + tokens.subList(index + 1, tokens.size)
        }
        index += 1
    }
    return tokens
}

fun parse(input: String) =
    input.mapNotNull { char ->
        when (char) {
            '[' -> Open
            ']' -> Close
            ',' -> null
            else -> Value(char.digitToInt())
        }
    }

fun List<Token>.unparse(): String =
    this.zipWithNext().map { (first, second) ->
        when (first) {
            Open -> '['
            Close -> when (second) {
                Close -> ']'
                Open -> "],"
                is Value -> "],"
            }
            is Value -> when (second) {
                Close -> first.value
                Open -> "${first.value},"
                is Value -> "${first.value},"
            }
        }
    }
        .let { it.joinToString("") + ']' }

sealed class Token {
    data class Value(val value: Int) : Token() {
        operator fun plus(other: Value) = Value(value + other.value)
        fun split(): Pair<Value, Value> {
            val halfRoundedDown = value / 2
            return Pair(Value(halfRoundedDown), Value(value - halfRoundedDown))
        }
    }

    sealed class Bracket : Token() {
        object Open : Bracket()
        object Close : Bracket()
    }
}
