package days.day16

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day16.txt").string()
//    val exampleInput = Reader("day16-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(986)
}

fun part1(input: String) = parseOnePacket(input.toBinary()).first.versionSum()

fun parseOnePacket(binary: List<Char>, isSubpacket: Boolean = false): Pair<Packet, List<Char>> {
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
                    val finalUnprocessed = unprocessed.drop(5).let {
                        if (isSubpacket) it
                        else it.drop((4 - (position + 5) % 4))
                    }
                    Pair(value, finalUnprocessed)
                }
            }

            val (value, unprocessed) = parseValue(body, emptyList(), 6)

            Pair(
                Packet.Literal(
                    version = version,
                    typeID = typeID,
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
                        val (subpacket, leftover) = parseOnePacket(unprocessed, isSubpacket = true)
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
                        val (subpacket, leftover) = parseOnePacket(unprocessed, isSubpacket = true)
                        parseSubpackets(leftover, subpackets + subpacket, subpacketCount - 1)
                    }
                }

                val numberOfSubpackets = rest.take(11).joinToString("").toInt(2)

                val (subpackets, unprocessed) = parseSubpackets(rest.drop(11), emptyList(), numberOfSubpackets)

                Pair(subpackets, unprocessed)
            }

            Pair(
                Packet.Operator(
                    version = version,
                    typeID = typeID,
                    subpackets = subpackets
                ),
                unprocessed
            )
        }
    }
}

sealed class Packet(open val version: Int, open val typeID: Int) {
    data class Literal(
        override val version: Int,
        override val typeID: Int,
        val value: Long,
    ) : Packet(version, typeID)

    data class Operator(
        override val version: Int,
        override val typeID: Int,
        val subpackets: List<Packet>,
    ) : Packet(version, typeID)

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

