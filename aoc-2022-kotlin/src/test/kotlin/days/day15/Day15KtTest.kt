package days.day15

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class Day15KtTest : StringSpec({
    "resolve overlapping ranges" {
        listOf(0..3, 2..5).resolve() shouldBe listOf(0..5)
    }

    "resolve overlapping ranges given in the wrong order" {
        listOf(2..5, 0..3).resolve() shouldBe listOf(0..5)
    }

    "resolve non-overlapping ranges" {
        listOf(0..2, 3..5).resolve() shouldBe listOf(0..2, 3..5)
    }

    "when one contains the other" {
        listOf(0..5, 1..2).resolve() shouldBe listOf(0..5)
    }

    "two overlapping and one distinct" {
        listOf(0..3, 2..5, 10..11).resolve() shouldBe listOf(0..5, 10..11)
    }

    "three overlapping" {
        listOf(0..3, 2..5, 4..6).resolve() shouldBe listOf(0..6)
    }

    "??" {
        listOf(-2..2, 2..14, 2..2, 12..12, 14..18, 16..24).resolve() shouldBe listOf(-2..24)
    }
})
