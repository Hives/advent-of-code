package days.day18

import days.day18.Node.Bracket.Close
import days.day18.Node.Bracket.Open
import days.day18.Node.Value
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

fun magnitude(nodes: List<Node>): Int {
    if (nodes.size == 1) return (nodes.single() as Value).value
    val (left, right) = constituents(nodes)
    return (3 * magnitude(left)) + (2 * magnitude(right))
}

fun constituents(nodes: List<Node>): Pair<List<Node>, List<Node>> {
    val stripped = nodes.drop(1).dropLast(1)

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

fun add(left: List<Node>, right: List<Node>) =
    reduceExpression((listOf(Open) + left + right + Close))

tailrec fun reduceExpression(nodes: List<Node>): List<Node> {
    val exploded = explode(nodes)
    if (exploded != nodes) return reduceExpression(exploded)
    val splitted = split(exploded)
    if (splitted != exploded) return reduceExpression(splitted)
    return nodes
}

fun explode(nodes: List<Node>): List<Node> {
    var index = 0
    var openCount = 0
    while (index < nodes.size) {
        if (nodes[index] == Open) openCount += 1
        if (nodes[index] == Close) openCount -= 1
        if (openCount == 5) {
            val mutableNodes = nodes.toMutableList()

            val left = nodes[index + 1] as Value
            val right = nodes[index + 2] as Value

            var indexL = index
            while (true) {
                if (indexL < 0) break
                if (mutableNodes[indexL] is Value) break
                indexL -= 1
            }
            if (indexL >= 0) {
                val oldValue = mutableNodes[indexL] as Value
                mutableNodes[indexL] = oldValue + left
            }

            var indexR = index + 3
            while (true) {
                if (indexR >= mutableNodes.size) break
                if (mutableNodes[indexR] is Value) break
                indexR += 1
            }
            if (indexR < mutableNodes.size) {
                val oldValue = mutableNodes[indexR] as Value
                mutableNodes[indexR] = oldValue + right
            }

            return mutableNodes.subList(0, index) + Value(0) + mutableNodes.subList(index + 4, mutableNodes.size)
        }
        index++
    }
    return nodes
}

fun split(nodes: List<Node>): List<Node> {
    var index = 0
    while (index < nodes.size) {
        val node = nodes[index]
        if (node is Value && node.value >= 10) {
            val (left, right) = node.split()
            return nodes.subList(0, index) + listOf(Open, left, right, Close) + nodes.subList(index + 1, nodes.size)
        }
        index += 1
    }
    return nodes
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

fun List<Node>.unparse(): String =
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

sealed class Node {
    data class Value(val value: Int) : Node() {
        operator fun plus(other: Value) = Value(value + other.value)
        fun split(): Pair<Value, Value> {
            val halfRoundedDown = value / 2
            return Pair(Value(halfRoundedDown), Value(value - halfRoundedDown))
        }
    }

    sealed class Bracket : Node() {
        object Open : Bracket()
        object Close : Bracket()
    }
}
