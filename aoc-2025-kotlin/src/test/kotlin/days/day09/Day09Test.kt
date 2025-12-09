package days.day09

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import lib.Vector
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day09Test {
    @Nested
    inner class Disqualifies {
        private val rect = Pair(Vector(2,2), Vector(5, 5))

        @Test
        fun `top`() {
            assertThat(Pair(Vector(3, 0), Vector(3, 3)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `bottom`() {
            assertThat(Pair(Vector(3, 3), Vector(3, 8)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `above top`() {
            assertThat(Pair(Vector(3, 0), Vector(3, 1)).disqualifies(rect)).isFalse()
        }
        @Test
        fun `below bottom`() {
            assertThat(Pair(Vector(3, 6), Vector(3, 9)).disqualifies(rect)).isFalse()
        }

        @Test
        fun `left`() {
            assertThat(Pair(Vector(0, 3), Vector(3, 3)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `right`() {
            assertThat(Pair(Vector(3, 3), Vector(8, 3)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `left of left`() {
            assertThat(Pair(Vector(0, 3), Vector(1, 3)).disqualifies(rect)).isFalse()
        }
        @Test
        fun `right of right`() {
            assertThat(Pair(Vector(6, 3), Vector(8, 3)).disqualifies(rect)).isFalse()
        }

        @Test
        fun `horizontal and inside`() {
            assertThat(Pair(Vector(3, 3), Vector(4, 3)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `vertical and inside`() {
            assertThat(Pair(Vector(3, 3), Vector(3, 4)).disqualifies(rect)).isTrue()
        }

        @Test
        fun `top edge pointing out`() {
            assertThat(Pair(Vector(3, 2), Vector(3, 0)).disqualifies(rect)).isFalse()
        }
        @Test
        fun `top edge pointing in`() {
            assertThat(Pair(Vector(3, 2), Vector(3, 4)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `bottom edge pointing out`() {
            assertThat(Pair(Vector(3, 5), Vector(3, 8)).disqualifies(rect)).isFalse()
        }
        @Test
        fun `bottom edge pointing in`() {
            assertThat(Pair(Vector(3, 5), Vector(3, 3)).disqualifies(rect)).isTrue()
        }

        @Test
        fun `left edge pointing out`() {
            assertThat(Pair(Vector(2, 3), Vector(0, 3)).disqualifies(rect)).isFalse()
        }
        @Test
        fun `left edge pointing in`() {
            assertThat(Pair(Vector(2, 3), Vector(4, 3)).disqualifies(rect)).isTrue()
        }
        @Test
        fun `right edge pointing out`() {
            assertThat(Pair(Vector(5, 3), Vector(8, 3)).disqualifies(rect)).isFalse()
        }
        @Test
        fun `right edge pointing in`() {
            assertThat(Pair(Vector(5, 3), Vector(3, 3)).disqualifies(rect)).isTrue()
        }

        @Test
        fun `lies on top edge`() {
            assertThat(Pair(Vector(3, 2), Vector(4, 2)).disqualifies(rect)).isFalse()
        }
    }
}
