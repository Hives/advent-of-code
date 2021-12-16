package days.day16

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day16.txt").string()

    time(message = "Part 1", iterations = 1_000, warmUpIterations = 100) {
        part1(input)
    }.checkAnswer(986)

    time(message = "Part 2", iterations = 1_000, warmUpIterations = 100) {
        part2(input)
    }.checkAnswer(18234816469452)
}

fun part1(input: String): Int {
    val (outerPacket, _) = parsePacket(input.toBinary())
    return outerPacket.versionSum()
}

fun part2(input: String): Long {
    val (outerPacket, _) = parsePacket(input.toBinary())
    return outerPacket.value
}

fun parsePacket(binary: String): Pair<Packet, String> {
    val version = binary.take(3).toInt(2)
    val typeID = binary.substring(3..5).toInt(2)

    val body = binary.substring(6)

    return when (typeID) {
        4 -> {
            val (value, unprocessed) = parseValue(body)

            Pair(Packet.Literal(version, value), unprocessed)
        }
        else -> {
            val lengthType = body[0]
            val rest = body.drop(1)

            val (subpackets, unprocessed) = when (lengthType) {
                '0' -> {
                    val lengthFieldSize = 15
                    val subpacketsSize = rest.take(lengthFieldSize).toInt(2)
                    val encodedSubpackets = rest.drop(lengthFieldSize).take(subpacketsSize)
                    val remainder = rest.drop(lengthFieldSize + subpacketsSize)
                    val subpackets = parseAllSubpackets(encodedSubpackets)

                    Pair(subpackets, remainder)
                }
                '1' -> {
                    val lengthFieldSize = 11
                    val subpacketsCount = rest.take(lengthFieldSize).toInt(2)
                    val (subpackets, remainder) = parseNSubpackets(rest.drop(lengthFieldSize), subpacketsCount)

                    Pair(subpackets, remainder)
                }
                else -> throw Exception("Unknown lengthType: $lengthType")
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

tailrec fun parseValue(
    unprocessed: String,
    processed: String = "",
    position: Int = 6,
): Pair<Long, String> {
    val chunk = unprocessed.take(5)
    return if (chunk[0] == '0') {
        val value = (processed + chunk.drop(1)).toLong(2)
        val finalUnprocessed = unprocessed.drop(5)
        Pair(value, finalUnprocessed)
    } else {
        parseValue(unprocessed.drop(5), processed + chunk.drop(1), position + 5)
    }
}

tailrec fun parseAllSubpackets(
    unprocessed: String,
    subpackets: List<Packet> = emptyList(),
): List<Packet> =
    if (unprocessed.isEmpty()) subpackets
    else {
        val (subpacket, remaining) = parsePacket(unprocessed)
        parseAllSubpackets(remaining, subpackets + subpacket)
    }

tailrec fun parseNSubpackets(
    unprocessed: String,
    n: Int,
    subpackets: List<Packet> = emptyList(),
): Pair<List<Packet>, String> {
    return if (n == 0) Pair(subpackets, unprocessed)
    else {
        val (subpacket, remaining) = parsePacket(unprocessed)
        parseNSubpackets(remaining, n - 1, subpackets + subpacket)
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
                get() = subpackets.fold(1L) { acc, packet -> acc * packet.value }
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

fun String.toBinary() = map {
    val int = Integer.parseInt(it.toString(), 16)
    Integer.toBinaryString(int).padStart(4, '0')
}.joinToString("")

