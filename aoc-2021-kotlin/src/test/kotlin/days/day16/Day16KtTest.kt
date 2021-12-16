package days.day16

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Day16KtTest : StringSpec({

    "Making a value literal" {
        val (packet, remaining) = parsePacket("D2FE28F".toBinary())

        packet shouldBe Packet.Literal(
            version = 6,
            value = 2021
        )
        remaining shouldBe "0001111".toList()
    }

    "operator example 1" {
        val (packet, remaining) = parsePacket("38006F45291200".toBinary())

        packet shouldBe Packet.Operator.LessThan(
            version = 1,
            subpackets = listOf(
                Packet.Literal(
                    version = 6,
                    value = 10
                ),
                Packet.Literal(
                    version = 2,
                    value = 20
                )
            )
        )
        remaining shouldBe "0000000".toList()
    }

    "operator example 2" {
        val (packet, remaining) = parsePacket("EE00D40C823060".toBinary())

        packet shouldBe Packet.Operator.Maximum(
            version = 7,
            subpackets = listOf(
                Packet.Literal(
                    version = 2,
                    value = 1
                ),
                Packet.Literal(
                    version = 4,
                    value = 2
                ),
                Packet.Literal(
                    version = 1,
                    value = 3
                )
            )
        )
        remaining shouldBe "00000".toList()
    }
})
