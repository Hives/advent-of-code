package days.day25

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class Day25KtTest : StringSpec({
    "snafu -> decimal" {
        forAll(
            row("1=-0-2", 1747),
            row("12111", 906),
            row("2=0=", 198),
            row("21", 11),
            row("2=01", 201),
            row("111", 31),
            row("20012", 1257),
            row("112", 32),
            row("1=-1=", 353),
            row("1-12", 107),
            row("12", 7),
            row("1=", 3),
            row("122", 37),
        ) { snafu, decimal ->
            toDecimal(snafu) shouldBe decimal
        }
    }
})
