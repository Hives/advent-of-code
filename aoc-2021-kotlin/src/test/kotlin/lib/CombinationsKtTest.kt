package lib

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CombinationsKtTest : StringSpec({

    "combinations 1" {
        listOf(1, 2, 3, 4).combinations(1) shouldBe listOf(
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(4),
        )
    }

    "combinations 2" {
        listOf(1, 2, 3, 4).combinations(3) shouldBe listOf(
            listOf(1, 2, 3),
            listOf(1, 2, 4),
            listOf(1, 3, 4),
            listOf(2, 3, 4),
        )
    }


    "combinations 3" {
        listOf(1, 2, 3, 4).combinations(2) shouldBe listOf(
            listOf(1, 2),
            listOf(1, 3),
            listOf(1, 4),
            listOf(2, 3),
            listOf(2, 4),
            listOf(3, 4),
        )
    }

    "empty list" {
        emptyList<Int>().combinations(2) shouldBe emptyList()
    }

    "n > list.size" {
        listOf(1, 2, 3).combinations(4) shouldBe emptyList()
    }
})
