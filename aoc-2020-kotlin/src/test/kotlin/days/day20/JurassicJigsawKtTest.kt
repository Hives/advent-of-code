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
                Tile.from("Tile 1:\nxx1\nxx2\nxx3"),
                Orientation(false, 0)
            )
            val tile2 = OrientedTile(
                Tile.from("Tile 2:\n1yy\n2yy\n3yy"),
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
                Tile.from("Tile 1:\n123\nxxx\nxxx"),
                Orientation(false, 0)
            )
            val tile2 = OrientedTile(
                Tile.from("Tile 2:\nyyy\nyyy\n321"),
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
                Tile.from("Tile 1:\nxxx\nxxx\n123"),
                Orientation(false, 0)
            )
            val tile2 = OrientedTile(
                Tile.from("Tile 2:\nyyy\nyyy\n123"),
                Orientation(true, 2)
            )
            assertThat(tile1.alignsWith(tile2, 2)).isTrue()

            assertThat(tile1.alignsWith(tile2, 0)).isFalse()
            assertThat(tile1.alignsWith(tile2, 1)).isFalse()
            assertThat(tile1.alignsWith(tile2, 3)).isFalse()
        }
    }
}