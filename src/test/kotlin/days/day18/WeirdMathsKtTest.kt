package days.day18

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class WeirdMathsKtTest {
    private val one = Value(1)
    private val two = Value(2)
    private val three = Value(3)
    private val four = Value(4)
    private val five = Value(5)
    private val six = Value(6)

    @Test
    fun `split a string`() {
        assertThat(splitString("((1 + 2) * 3) + (4 * (5 + 6))")).isEqualTo(listOf(
            "(", "(", "1", "+", "2", ")", "*", "3", ")", "+", "(", "4", "*", "(", "5", "+", "6", ")", ")"
        ))

    }

    @Test
    fun `evaluate a combination`() {
        val expression =
            Combination(
                Combination(
                    Combination(
                        Combination(
                            Combination(one, And, two),
                            Times, three
                        ),
                        And, four
                    ),
                    Times, five
                ),
                And, six
            )
        assertThat(expression.evaluate().value).isEqualTo(71)
    }

    @Test
    fun `parse simple expressions`() {
        assertThat(parseSymbols(splitString("1 + 2"))).isEqualTo(Combination(one, And, two))
        assertThat(parseSymbols(splitString("3 * 4"))).isEqualTo(Combination(three, Times, four))
        assertThat(parseSymbols(splitString("1 + 2 * 3 + 4 * 5 + 6"))).isEqualTo(
            Combination(
                Combination(
                    Combination(
                        Combination(
                            Combination(one, And, two),
                            Times, three
                        ),
                        And, four
                    ),
                    Times, five
                ),
                And, six
            )
        )
    }

    @Test
    fun `parse expression with brackets`() {
        assertThat(parseSymbols(splitString("1 + (2 * 3) + (4 * (5 + 6))"))).isEqualTo(
            Combination(
                Combination(
                    one,
                    And,
                    Combination(two, Times, three)
                ),
                And,
                Combination(
                    four,
                    Times,
                    Combination(five, And, six)
                )
            )
        )
    }

    @Nested
    inner class Examples {
        @Test
        fun `example 1`() {
            assertThat(parseSymbols(splitString("2 * 3 + (4 * 5)")).evaluate().value).isEqualTo(26)
        }
        @Test
        fun `example 2`() {
            assertThat(parseSymbols(splitString("5 + (8 * 3 + 9 + 3 * 4 * 3)")).evaluate().value).isEqualTo(437)
        }
        @Test
        fun `example 3`() {
            assertThat(parseSymbols(splitString("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))")).evaluate().value)
                .isEqualTo(12240)
        }
        @Test
        fun `example 4`() {
            assertThat(parseSymbols(splitString("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2")).evaluate().value)
                .isEqualTo(13632)
        }
    }
}