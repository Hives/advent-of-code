package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class AddKtTest {
    @Test
    fun `it can add`() {
        assertThat(add(1, 1)).isEqualTo(2)
    }
}