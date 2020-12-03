package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

internal class TobogganKtTest {
    private val terrain = Reader("day-3-toboggan-terrain.txt").strings()

    private fun createToboggan() = Toboggan(terrain, 3, 1)

    @Test
    fun `init`() {
        val t = createToboggan()
        assertThat(t.x).isEqualTo(0)
        assertThat(t.y).isEqualTo(0)
        assertThat(t.path).isEqualTo(".")
    }

    @Test
    fun `one move`() {
        val t = createToboggan().move()
        assertThat(t.x).isEqualTo(3)
        assertThat(t.y).isEqualTo(1)
        assertThat(t.path).isEqualTo("..")
    }

    @Test
    fun `two moves`() {
        val t = createToboggan().move().move()
        assertThat(t.x).isEqualTo(6)
        assertThat(t.y).isEqualTo(2)
        assertThat(t.path).isEqualTo("..#")
    }

    @Test
    fun `three moves`() {
        val t = createToboggan().move().move().move()
        assertThat(t.x).isEqualTo(9)
        assertThat(t.y).isEqualTo(3)
        assertThat(t.path).isEqualTo("..#.")
    }

    @Test
    fun `four moves`() {
        val t = createToboggan().move().move().move().move()
        assertThat(t.x).isEqualTo(1)
        assertThat(t.y).isEqualTo(4)
        assertThat(t.path).isEqualTo("..#.#")
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
        val t = createToboggan()
        assertThat(t.go()).isEqualTo("..#.##.####")
    }
}
