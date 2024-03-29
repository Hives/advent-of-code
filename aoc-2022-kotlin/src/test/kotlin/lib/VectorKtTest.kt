package lib

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import lib.Comparisons.EQUAL
import lib.Comparisons.GREATER_THAN
import lib.Comparisons.LESS_THAN

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

    "subtractin" {
        Vector(3, -2) - Vector(-1, 5) shouldBe Vector(4, -7)
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
        ) { quarterTurns, expected ->
            v.rotateQuarterTurnsCCW(quarterTurns) shouldBe expected
        }
    }

    "isAdjacentTo" {
        forAll(
            row(Vector(0, 0), Vector(0, 1), true),
            row(Vector(0, 0), Vector(1, 0), true),
            row(Vector(0, 0), Vector(1, 1), true),
            row(Vector(0, 0), Vector(1, -1), true),
            row(Vector(0, 0), Vector(0, 2), false),
            row(Vector(0, 0), Vector(-2, 0), false),
            row(Vector(0, 0), Vector(2, 2), false),
            row(Vector(0, 0), Vector(2, -2), false),
        ) { v1, v2, expected ->
            v1.isAdjacentTo(v2) shouldBe expected
            v2.isAdjacentTo(v1) shouldBe expected
        }
    }

    "path to" {
        forAll(
            row(Vector(0, 1), Vector(0, 3), listOf(Vector(0, 1), Vector(0, 2), Vector(0, 3))),
            row(Vector(0, 3), Vector(0, 1), listOf(Vector(0, 3), Vector(0, 2), Vector(0, 1))),
            row(Vector(1, 0), Vector(3, 0), listOf(Vector(1, 0), Vector(2, 0), Vector(3, 0))),
            row(Vector(3, 0), Vector(1, 0), listOf(Vector(3, 0), Vector(2, 0), Vector(1, 0))),
        ) { start, end, expected ->
            start.pathTo(end) shouldBe expected
        }
    }

    "pathTo throws if points aren't horizontally or vertically aligned" {
        shouldThrow<Exception> {
            Vector(0, 0).pathTo(Vector(1, 2))
        }
    }

    "orderin" {
        // order in reading order
        val topLeft = Vector(0, 0)
        val topRight = Vector(1, 0)
        val bottomLeft = Vector(0, 1)
        forAll(
            row(topLeft, LESS_THAN, topRight),
            row(topRight, GREATER_THAN, topLeft),
            row(topLeft, LESS_THAN, bottomLeft),
            row(bottomLeft, GREATER_THAN, topLeft),
            row(topRight, LESS_THAN, bottomLeft),
            row(bottomLeft, GREATER_THAN, topLeft),
            row(topLeft, EQUAL, topLeft)
        ) { v1, comparison, v2 ->
            comparison.compare(v1, v2) shouldBe true
        }
    }

    "should sort into 'read order'" {
        val topLeft = Vector(4, 9)
        val middleLeft = Vector(4, 10)
        val bottomLeft = Vector(4, 11)
        val topMiddle = Vector(5, 9)
        val middleMiddle = Vector(5, 10)
        val bottomMiddle = Vector(5, 11)
        val topRight = Vector(6, 9)
        val middleRight = Vector(6, 10)
        val bottomRight = Vector(6, 11)

        val sortedPoints = listOf(
            topLeft,
            topMiddle,
            topRight,
            middleLeft,
            middleMiddle,
            middleRight,
            bottomLeft,
            bottomMiddle,
            bottomRight
        )

        sortedPoints.shuffled().sorted() shouldBe sortedPoints
    }

})

private enum class Comparisons {
    GREATER_THAN {
        override fun <T : Comparable<T>> compare(left: T, right: T) = left > right
    },
    LESS_THAN {
        override fun <T : Comparable<T>> compare(left: T, right: T) = left < right
    },
    EQUAL {
        override fun <T : Comparable<T>> compare(left: T, right: T) = left == right
    };

    abstract fun <T : Comparable<T>> compare(left: T, right: T): Boolean
}
