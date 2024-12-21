package days.day21

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import lib.Vector
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day21KtTest {
    @Nested
    inner class Permutations {
        @Test
        fun `should return permutations`() {
            val xs = listOf(1, 2, 3)
            assertThat(xs.permutations()).containsExactlyInAnyOrder(
                listOf(1, 2, 3),
                listOf(1, 3, 2),
                listOf(2, 1, 3),
                listOf(2, 3, 1),
                listOf(3, 1, 2),
                listOf(3, 2, 1),
            )
        }

        @Test
        fun `should work for list of 1`() {
            val xs = listOf(1)
            assertThat(xs.permutations()).containsOnly(listOf(1))
        }

        @Test
        fun `no duplicates`() {
            val xs = listOf(1, 1, 2)
            val perms = xs.permutations()
            assertThat(perms).containsExactlyInAnyOrder(
                listOf(1, 1, 2),
                listOf(1, 2, 1),
                listOf(2, 1, 1),
            )
            assertThat(perms.size).isEqualTo(3)
        }
    }

    @Nested
    inner class GetPathsForVector {
        val r = Vector(1, 0)
        val u = Vector(0, -1)

        @Test
        fun `1`() {
            val v = Vector(3, -2)
            assertThat(getPaths(v)).containsExactlyInAnyOrder(
                listOf(u, u, r, r, r),
                listOf(u, r, u, r, r),
                listOf(u, r, r, u, r),
                listOf(u, r, r, r, u),
                listOf(r, u, u, r, r),
                listOf(r, u, r, u, r),
                listOf(r, u, r, r, u),
                listOf(r, r, u, u, r),
                listOf(r, r, u, r, u),
                listOf(r, r, r, u, u),
            )
        }

        @Test
        fun `returns empty list for 0 vector`() {
            assertThat(getPaths(Vector(0, 0))).isEqualTo(setOf(emptyList()))
        }
    }

    @Nested
    inner class GetPathsForPad {
        val l = Vector(-1, 0)
        val u = Vector(0, -1)

        @Test
        fun `1`() {
            val paths = getPaths(
                initial = Vector(2, 3),
                final = Vector(0, 1),
                verboten = Vector(0, 3)
            )
            assertThat(paths).containsExactlyInAnyOrder(
                listOf(u, u, l, l),
                listOf(u, l, u, l),
                listOf(u, l, l, u),
                listOf(l, u, u, l),
                listOf(l, u, l, u),
//                listOf(l, l, u, u), -> is verboten
            )
        }
    }

    @Nested
    inner class ButtonsToDirections {
        @Test
        fun `1`() {
            val buttonPresses = listOf('0', '2', '9', 'A')
            val initial = Vector(2, 3)

            val actual = buttonsToDirections(buttonPresses, numberPad, initial)

            assertThat(actual).containsExactlyInAnyOrder(
                listOf('<', 'A', '^', 'A', '>', '^', '^', 'A', 'v', 'v', 'v', 'A'),
                listOf('<', 'A', '^', 'A', '^', '>', '^', 'A', 'v', 'v', 'v', 'A'),
                listOf('<', 'A', '^', 'A', '^', '^', '>', 'A', 'v', 'v', 'v', 'A')
            )
        }

        @Test
        fun `2`() {
            val buttonPresses = listOf('<', 'A', '^', 'A', '>', '^', '^', 'A', 'v', 'v', 'v', 'A')
            val initial = Vector(2, 0)

            val actual = buttonsToDirections(buttonPresses, directionPad, initial)

            assertThat(actual).contains(
                listOf(
                    'v', '<', '<', 'A', '>', '>',
                    '^', 'A', '<', 'A', '>', 'A',
                    'v', 'A', '<', '^', 'A', 'A',
                    '>', 'A', '<', 'v', 'A', 'A',
                    'A', '>', '^', 'A'
                ),
            )
        }
    }
}
