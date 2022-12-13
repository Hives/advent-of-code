package days.day13

import days.day13.OrderStatus.DONT_KNOW
import days.day13.OrderStatus.RIGHT_ORDER
import days.day13.OrderStatus.WRONG_ORDER
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day13.txt").string()
    val exampleInput = Reader("day13-example.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(5684)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(22932)
}

fun part1(input: String) =
    parse(input).map { it.compare() }
        .foldIndexed(0) { index, acc, orderStatus ->
            if (orderStatus == RIGHT_ORDER) acc + (index + 1)
            else acc
        }

fun part2(input: String): Int {
    val packets = input.replace("\n\n", "\n").split("\n").map(::parsePacket)
    val dividerPacket1 = parsePacket("[[2]]")
    val dividerPacket2 = parsePacket("[[6]]")
    val allPackets = packets + dividerPacket1 + dividerPacket2

    return allPackets.sorted().let {
        (it.indexOf(dividerPacket1) + 1) * (it.indexOf(dividerPacket2) + 1)
    }
}

fun Pair<Element, Element>.compare(): OrderStatus {
    val (left, right) = this
    return when {
        left is Element.Number && right is Element.Number -> {
            when {
                left.value < right.value -> RIGHT_ORDER
                left.value > right.value -> WRONG_ORDER
                else -> DONT_KNOW
            }
        }

        left is Element.ElementList && right is Element.ElementList -> {
            when {
                left.elements.isEmpty() && right.elements.isEmpty() -> DONT_KNOW
                left.elements.isEmpty() -> RIGHT_ORDER
                right.elements.isEmpty() -> WRONG_ORDER
                else -> {
                    when (Pair(left.head, right.head).compare()) {
                        RIGHT_ORDER -> RIGHT_ORDER
                        WRONG_ORDER -> WRONG_ORDER
                        DONT_KNOW -> Pair(left.tail, right.tail).compare()
                    }
                }
            }
        }

        left is Element.Number -> Pair(Element.ElementList(listOf(left)), right).compare()
        else -> Pair(left, Element.ElementList(listOf(right))).compare()
    }
}

enum class OrderStatus { RIGHT_ORDER, WRONG_ORDER, DONT_KNOW }

fun parse(input: String): List<Pair<Element, Element>> =
    input.split("\n\n")
        .map {
            it.split("\n")
                .map(::parsePacket).let { (first, second) -> Pair(first, second) }
        }

fun parsePacket(input: String): Element =
    input.fold(Pair("", 0)) { (acc, bracketDepth), c ->
        when {
            c.isDigit() -> Pair(acc + c, bracketDepth)
            c == '[' -> Pair(acc + c, bracketDepth + 1)
            c == ']' -> Pair(acc + c, bracketDepth - 1)
            c == ',' -> if (bracketDepth == 1) Pair("$acc!", bracketDepth) else Pair(acc + c, bracketDepth)
            else -> throw Exception("Unknown character: $c")
        }
    }.first.substring(1 until input.length - 1).split('!')
        .map { chunk ->
            when {
                chunk == "" -> Element.ElementList(emptyList())
                chunk.first().isDigit() -> Element.Number(chunk.toInt())
                chunk.first() == '[' -> parsePacket(chunk)
                else -> throw Exception("Unparseable chunk: $chunk")
            }
        }
        .let { Element.ElementList(it) }

sealed class Element : Comparable<Element> {
    override fun compareTo(other: Element): Int =
        when (Pair(this, other).compare()) {
            RIGHT_ORDER -> -1
            WRONG_ORDER -> 1
            DONT_KNOW -> 0
        }

    data class Number(val value: Int) : Element()
    data class ElementList(val elements: List<Element>) : Element() {
        val head
            get() = elements.first()
        val tail
            get() = ElementList(elements.drop(1))
    }
}
