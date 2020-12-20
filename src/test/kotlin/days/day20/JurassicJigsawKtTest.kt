package days.day20

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class JurassicJigsawKtTest {
    @Nested
    inner class CheckingAlignment {
        @Test
        fun `correct match`() {
            val tile1 = OrientedTile(
                parseTile("xx1\nxx2\nxx3", 1),
                Orientation(false, 0)
            )
            val tile2 = OrientedTile(
                parseTile("1yy\n2yy\n3yy", 2),
                Orientation(false, 0)
            )
            assertThat(tile1.alignsWith(tile2, 1)).isTrue()

            assertThat(tile1.alignsWith(tile2, 0)).isFalse()
            assertThat(tile1.alignsWith(tile2, 2)).isFalse()
            assertThat(tile1.alignsWith(tile2, 3)).isFalse()
        }

        @Test
        fun `correct match when flipped`() {
            val tile1 = OrientedTile(
                parseTile("123\nxxx\nxxx", 1),
                Orientation(false, 0)
            )
            val tile2 = OrientedTile(
                parseTile("yyy\nyyy\n321", 2),
                Orientation(true, 0)
            )
            assertThat(tile1.alignsWith(tile2, 0)).isTrue()

            assertThat(tile1.alignsWith(tile2, 1)).isFalse()
            assertThat(tile1.alignsWith(tile2, 2)).isFalse()
            assertThat(tile1.alignsWith(tile2, 3)).isFalse()
        }

        @Test
        fun `correct match when flipped and rotated`() {
            val tile1 = OrientedTile(
                parseTile("xxx\nxxx\n123", 1),
                Orientation(false, 0)
            )
            val tile2 = OrientedTile(
                parseTile("yyy\nyyy\n123", 2),
                Orientation(true, 2)
            )
            assertThat(tile1.alignsWith(tile2, 2)).isTrue()

            assertThat(tile1.alignsWith(tile2, 0)).isFalse()
            assertThat(tile1.alignsWith(tile2, 1)).isFalse()
            assertThat(tile1.alignsWith(tile2, 3)).isFalse()
        }
    }
}