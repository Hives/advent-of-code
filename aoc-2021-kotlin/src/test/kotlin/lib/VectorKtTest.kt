package lib

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class VectorKtTest : StringSpec({
    "manhattan distance" {
        forAll(
            row(Vector(100, -10), 110),
            row(Vector(-200, 20), 220)
        ) { vector, expectedDistance ->
            vector.manhattanDistance shouldBe expectedDistance
        }
    }

    "multiplyin" {
        forAll(
            row(Vector(1, 2), -3, Vector(-3, -6)),
            row(Vector(-10, -11), 2, Vector(-20, -22))
        ) { v, multiplier, expected ->
            v * multiplier shouldBe expected
        }
    }

    "addin" {
        Vector(3, -2) + Vector(-1, 5) shouldBe Vector(2, 3)
    }

    "rotatin" {
        val v = Vector(3, 2)
        forAll(
            row(0, v),
            row(1, Vector(-2, 3)),
            row(2, Vector(-3, -2)),
            row(3, Vector(2, -3)),
            row(-1, Vector(2, -3)),
            row(-2, Vector(-3, -2)),
            row(-3, Vector(-2, 3)),
            row(400, v)
        ) { ccwQuarterTurns, expected ->
            v.rotateQuarterTurnsCCW(ccwQuarterTurns) shouldBe expected
        }
    }

})
