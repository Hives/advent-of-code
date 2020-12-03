package lib

class Toboggan(private val terrain: List<String>, private val slopeR: Int, private val slopeL: Int) {
    var x: Int = 0
    var y: Int = 0
    var path: String = ""

    val isFinished: Boolean
        get() = y >= terrain.size - 1

    private val width = terrain.first().length

    init {
        addPosToPath()
    }

    fun move(): Toboggan {
        x = (x + slopeR) % width
        y += slopeL
        addPosToPath()
        return this
    }

    fun go(): String {
        while (!isFinished) {
            move()
        }
        return path
    }

    private fun addPosToPath() {
        path += terrain[y][x]
    }
}
