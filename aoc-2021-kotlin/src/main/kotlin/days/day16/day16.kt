package days.day16

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day16.txt").string()

    time(message = "Part 1", warmUpIterations = 50) {
        part1(input)
    }.checkAnswer(986)

    time(message = "Part 2", warmUpIterations = 50) {
        part2(input)
    }.checkAnswer(18234816469452)
}

fun part1(input: String) = parseOnePacket(input.toBinary()).first.versionSum()
fun part2(input: String) = parseOnePacket(input.toBinary()).first.value

fun parseOnePacket(binary: List<Char>): Pair<Packet, List<Char>> {
    val version = binary.subList(0, 3).joinToString("").toInt(2)
    val typeID = binary.subList(3, 6).joinToString("").toInt(2)
    val body = binary.drop(6)

    return when (typeID) {
        4 -> {
            tailrec fun parseValue(
                unprocessed: List<Char>,
                processed: List<Char>,
                position: Int,
            ): Pair<Long, List<Char>> {
                val chunk = unprocessed.take(5)
                return if (chunk.first() == '1') {
                    parseValue(unprocessed.drop(5), processed + chunk.drop(1), position + 5)
                } else {
                    val value = (processed + chunk.drop(1)).joinToString("").toLong(2)
                    val finalUnprocessed = unprocessed.drop(5)
                    Pair(value, finalUnprocessed)
                }
            }

            val (value, unprocessed) = parseValue(body, emptyList(), 6)

            Pair(
                Packet.Literal(
                    version = version,
                    value = value
                ),
                unprocessed
            )
        }
        else -> {
            val lengthType = body.first()
            val rest = body.drop(1)

            val (subpackets, unprocessed) = if (lengthType == '0') {
                tailrec fun parseSubpackets(
                    unprocessed: List<Char>,
                    subpackets: List<Packet>,
                ): List<Packet> =
                    if (unprocessed.isEmpty()) subpackets
                    else {
                        val (subpacket, leftover) = parseOnePacket(unprocessed)
                        parseSubpackets(leftover, subpackets + subpacket)
                    }

                val lengthOfSubpackets = rest.take(15).joinToString("").toInt(2)
                val encodedSubpackets = rest.drop(15).take(lengthOfSubpackets)
                val subpackets = parseSubpackets(encodedSubpackets, emptyList())

                Pair(subpackets, rest.drop(15).drop(lengthOfSubpackets))
            } else {
                tailrec fun parseSubpackets(
                    unprocessed: List<Char>,
                    subpackets: List<Packet>,
                    subpacketCount: Int,
                ): Pair<List<Packet>, List<Char>> {
                    return if (subpacketCount == 0) Pair(subpackets, unprocessed)
                    else {
                        val (subpacket, leftover) = parseOnePacket(unprocessed)
                        parseSubpackets(leftover, subpackets + subpacket, subpacketCount - 1)
                    }
                }

                val numberOfSubpackets = rest.take(11).joinToString("").toInt(2)

                val (subpackets, unprocessed) = parseSubpackets(rest.drop(11), emptyList(), numberOfSubpackets)

                Pair(subpackets, unprocessed)
            }

            val packet = when (typeID) {
                0 -> Packet.Operator.Sum(version, subpackets)
                1 -> Packet.Operator.Product(version, subpackets)
                2 -> Packet.Operator.Minimum(version, subpackets)
                3 -> Packet.Operator.Maximum(version, subpackets)
                5 -> Packet.Operator.GreaterThan(version, subpackets)
                6 -> Packet.Operator.LessThan(version, subpackets)
                7 -> Packet.Operator.EqualTo(version, subpackets)
                else -> throw Exception("Unknown typeID: $typeID")
            }

            Pair(packet, unprocessed)
        }
    }
}

sealed class Packet(open val version: Int) {
    abstract val value: Long

    data class Literal(
        override val version: Int,
        override val value: Long,
    ) : Packet(version)

    sealed class Operator(
        override val version: Int,
        open val subpackets: List<Packet>,
    ) : Packet(version) {
        data class Sum(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = subpackets.sumOf { it.value }
        }

        data class Product(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = subpackets.fold(1L) { runningTotal, packet -> runningTotal * packet.value }
        }

        data class Minimum(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = subpackets.minOf { it.value }
        }

        data class Maximum(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = subpackets.maxOf { it.value }
        }

        data class GreaterThan(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = if (subpackets[0].value > subpackets[1].value) 1L else 0L
        }

        data class LessThan(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = if (subpackets[0].value < subpackets[1].value) 1L else 0L
        }

        data class EqualTo(
            override val version: Int,
            override val subpackets: List<Packet>,
        ) : Operator(version, subpackets) {
            override val value
                get() = if (subpackets[0].value == subpackets[1].value) 1L else 0L
        }
    }

    fun versionSum(): Int =
        when (this) {
            is Literal -> version
            is Operator -> version + subpackets.sumOf { it.versionSum() }
        }
}


fun String.toBinary() = flatMap {
    val int = Integer.parseInt(it.toString(), 16)
    val bin = Integer.toBinaryString(int).padStart(4, '0')
    bin.toList()
}

