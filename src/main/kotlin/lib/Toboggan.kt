package lib

class Toboggan(private val terrain: List<String>, private val slopeR: Int, private val slopeD: Int) {
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
        x = (x + slopeR) % width
        y += slopeD
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
