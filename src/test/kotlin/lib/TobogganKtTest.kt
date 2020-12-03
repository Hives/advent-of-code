package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

internal class TobogganKtTest {
    private val terrain = Reader("day-3-example.txt").strings()

    private fun createToboggan() = Toboggan(terrain, Slope(3, 1))

    @Test
    fun `init`() {
        val t = createToboggan()
        assertThat(t.x).isEqualTo(0)
        assertThat(t.y).isEqualTo(0)
        assertThat(t.path).isEqualTo(".")
        assertThat(t.trees).isEqualTo(0)
    }

    @Test
    fun `one move`() {
        val t = createToboggan().move()
        assertThat(t.x).isEqualTo(3)
        assertThat(t.y).isEqualTo(1)
        assertThat(t.path).isEqualTo("..")
        assertThat(t.trees).isEqualTo(0)
    }

    @Test
    fun `two moves`() {
        val t = createToboggan().move().move()
        assertThat(t.x).isEqualTo(6)
        assertThat(t.y).isEqualTo(2)
        assertThat(t.path).isEqualTo("..#")
        assertThat(t.trees).isEqualTo(1)
    }

    @Test
    fun `three moves`() {
        val t = createToboggan().move().move().move()
        assertThat(t.x).isEqualTo(9)
        assertThat(t.y).isEqualTo(3)
        assertThat(t.path).isEqualTo("..#.")
        assertThat(t.trees).isEqualTo(1)
    }

    @Test
    fun `four moves`() {
        val t = createToboggan().move().move().move().move()
        assertThat(t.x).isEqualTo(1)
        assertThat(t.y).isEqualTo(4)
        assertThat(t.path).isEqualTo("..#.#")
        assertThat(t.trees).isEqualTo(2)
    }

    @Test
    fun `can tell if finished`() {
        val t = createToboggan()
        repeat(9) { t.move() }
        assertThat(t.isFinished).isFalse()
        t.move()
        assertThat(t.isFinished).isTrue()
    }

    @Test
    fun `can run the whole trip`() {
        val t = createToboggan().go()
        assertThat(t.path).isEqualTo("..#.##.####")
        assertThat(t.trees).isEqualTo(7)
    }
}
