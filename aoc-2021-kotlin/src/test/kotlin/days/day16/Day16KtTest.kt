package days.day16

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Day16KtTest : StringSpec({

    "Making a value literal" {
        val (packet, remaining) = parseOnePacket("D2FE28F".toBinary())

        packet shouldBe Packet.Literal(
            version = 6,
            typeID = 4,
            value = 2021
        )
        remaining shouldBe "1111".toList()
    }

    "example 1" {
        val (packet, remaining) = parseOnePacket("38006F45291200".toBinary())

        packet shouldBe Packet.Operator(
            version = 1,
            typeID = 6,
            subpackets = listOf(
                Packet.Literal(
                    version = 6,
                    typeID = 4,
                    value = 10
                ),
                Packet.Literal(
                    version = 2,
                    typeID = 4,
                    value = 20
                )
            )
        )
        remaining shouldBe "0000000".toList()
    }

    "example 2" {
        val (packet, remaining) = parseOnePacket("EE00D40C823060".toBinary())

        packet shouldBe Packet.Operator(
            version = 7,
            typeID = 3,
            subpackets = listOf(
                Packet.Literal(
                    version = 2,
                    typeID = 4,
                    value = 1
                ),
                Packet.Literal(
                    version = 4,
                    typeID = 4,
                    value = 2
                ),
                Packet.Literal(
                    version = 1,
                    typeID = 4,
                    value = 3
                )
            )
        )
        remaining shouldBe "00000".toList()
    }
})
