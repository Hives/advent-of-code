package days.day24.part2

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import lib.Vector3Double
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day24Part2KtTest {
    @Nested
    inner class LineIntersections {
        @Test
        fun `x = 1 crosses y = 1`() {
            val line1 = Line(Vector3Double(1, 0, 0), Vector3Double(0, 1, 0))
            val line2 = Line(Vector3Double(0, 1, 0), Vector3Double(1, 0, 0))
            val intersection = findIntersection(line1, line2)
            assertThat(intersection).isEqualTo(Vector3Double(1, 1, 0))
        }

        @Test
        fun `angled line`() {
            val line1 = Line(Vector3Double(0, 0, 0), Vector3Double(0.5, 0.5, 0.5))
            val line2 = Line(Vector3Double(1, 0, 1), Vector3Double(0, 1, 0))
            val intersection = findIntersection(line1, line2)
            assertThat(intersection).isEqualTo(Vector3Double(1, 1, 1))
        }
    }

    @Nested
    inner class LinePlaneIntersections {
        @Test
        fun `line and plane`() {
            val line = Line(
                point = Vector3Double(0, 0, 0),
                direction = Vector3Double(1, 2, 3)
            )
            val plane = Plane(
                point = Vector3Double(10, 0, 0),
                span1 = Vector3Double(0, 1, 0),
                span2 = Vector3Double(0, 0, 2),
            )
            val intersection = findIntersection(line, plane)
            assertThat(intersection.x).isCloseTo(10.0, 0.00000000001)
            assertThat(intersection.y).isCloseTo(20.0, 0.00000000001)
            assertThat(intersection.z).isCloseTo(30.0, 0.00000000001)
        }
    }

    @Nested
    inner class CrossProduct {
        @Test
        fun `x axis cross y axis`() {
            val v1 = Vector3Double(2, 0, 0)
            val v2 = Vector3Double(0, 2, 0)
            assertThat(v1.cross(v2)).isEqualTo(Vector3Double(0, 0, 4))
            assertThat(v2.cross(v1)).isEqualTo(Vector3Double(0, 0, -4))
        }

        @Test
        fun `y axis cross z axis`() {
            val v1 = Vector3Double(0, 1, 0)
            val v2 = Vector3Double(0, 0, 1)
            assertThat(v1.cross(v2)).isEqualTo(Vector3Double(1, 0, 0))
            assertThat(v2.cross(v1)).isEqualTo(Vector3Double(-1, 0, 0))
        }

        @Test
        fun `x axis cross z axis`() {
            val v1 = Vector3Double(3, 0, 0)
            val v2 = Vector3Double(0, 0, 1)
            assertThat(v1.cross(v2)).isEqualTo(Vector3Double(0, -3, 0))
            assertThat(v2.cross(v1)).isEqualTo(Vector3Double(0, 3, 0))
        }
    }
}
