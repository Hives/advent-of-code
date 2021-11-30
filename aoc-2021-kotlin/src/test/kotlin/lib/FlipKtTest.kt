package lib

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FlipKtTest : StringSpec({
    "should flip a 2d list" {
        val list = listOf(
            listOf(10, 11, 12),
            listOf(20, 21, 22)
        )
        val flipped = list.flip()

        flipped shouldBe listOf(
            listOf(10, 20),
            listOf(11, 21),
            listOf(12, 22)
        )
    }
})