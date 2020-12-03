package lib

class Toboggan(private val terrain: List<String>, private val slope: Slope) {
    var x: Int = 0
        private set
    var y: Int = 0
        private set
    var path: String = ""
        private set

    val isFinished: Boolean
        get() = y >= terrain.size - 1

    val trees: Int
        get() = path.filter { it == '#' }.count()

    private val width = terrain.first().length

    init {
        addPosToPath()
    }

    fun move(): Toboggan {
        x = (x + slope.x) % width
        y += slope.y
        addPosToPath()
        return this
    }

    fun go(): Toboggan {
        while (!isFinished) move()
        return this
    }

    private fun addPosToPath() {
        path += terrain[y][x]
    }
}

data class Slope(val x: Int, val y: Int)
