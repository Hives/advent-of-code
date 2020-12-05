package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class SeatsKtTest {
    @Test
    fun `examples`() {
        listOf(
            Triple("FBFBBFFRLR", Seat(44, 5), 357),
            Triple("BFFFBBFRRR", Seat(70, 7), 567),
            Triple("FFFBBBFRRR", Seat(14, 7), 119),
            Triple("BBFFBBFRLL", Seat(102, 4), 820)
        ).forEach { (input, expectedSeat, expectedSeatId) ->
            val seat = findSeat(input)
            assertThat(seat).isEqualTo(expectedSeat)
            assertThat(seat.id).isEqualTo(expectedSeatId)
        }
    }
}