package days.day13

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class BusesPart1KtTest {
    @Test
    fun `returns time if time is a multiple of bus id`() {
        assertThat(getFirstBusDepartureAfterTime(20, 100)).isEqualTo(100)
    }

    @Test
    fun `returns lowest multiple of busId which is greater than or equal to time`() {
        assertThat(getFirstBusDepartureAfterTime(20, 99)).isEqualTo(100)
        assertThat(getFirstBusDepartureAfterTime(20, 81)).isEqualTo(100)
    }
}