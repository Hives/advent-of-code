package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class SeatsKtTest {
    @Test
    fun `examples`() {
        listOf(
            Pair("FBFBBFFRLR", 357),
            Pair("BFFFBBFRRR", 567),
            Pair("FFFBBBFRRR", 119),
            Pair("BBFFBBFRLL", 820)
        ).forEach { (input, expectedSeatId) ->
            assertThat(findSeat(input).id).isEqualTo(expectedSeatId)
        }
    }
}