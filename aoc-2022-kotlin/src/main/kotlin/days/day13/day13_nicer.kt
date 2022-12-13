package days.day13

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day13.txt").string()
    val exampleInput = Reader("day13-example.txt").string()

    time(message = "Part 1", iterations = 500) {
        Bar.part1(input)
    }.checkAnswer(5684)

    time(message = "Part 2", iterations = 500) {
        Bar.part2(input)
    }.checkAnswer(22932)
}

object Bar {
    fun part1(input: String) =
        parse(input).foldIndexed(0) { index, acc, (first, second) ->
            if (first < second) acc + (index + 1)
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
        data class Number(val value: Int) : Element()

        data class ElementList(val elements: List<Element>) : Element() {
            val head
                get() = elements.first()
            val tail
                get() = ElementList(elements.drop(1))
        }

        override fun compareTo(other: Element): Int {
            val (left, right) = Pair(this, other)

            return when {
                left is Number && right is Number -> left.value.compareTo(right.value)

                left is ElementList && right is ElementList -> {
                    when {
                        left.elements.isNotEmpty() && right.elements.isNotEmpty() -> {
                            left.head.compareTo(right.head).let {
                                if (it != 0) it
                                else left.tail.compareTo(right.tail)
                            }
                        }

                        left.elements.isEmpty() && right.elements.isEmpty() -> {
                            0
                        }

                        else -> {
                            if (left.elements.isEmpty()) -1 else 1
                        }
                    }
                }

                left is Number -> {
                    val leftWrapped = ElementList(listOf(left))
                    leftWrapped.compareTo(right)
                }

                else -> {
                    val rightWrapped = ElementList(listOf(right))
                    left.compareTo(rightWrapped)
                }
            }
        }
    }
}

