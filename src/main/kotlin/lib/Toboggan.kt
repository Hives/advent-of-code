package lib

class Toboggon(private val terrain: List<String>) {
    var x: Int = 0
    var y: Int = 0
    var path: String = ""

    val isFinished: Boolean
        get() = y >= terrain.size - 1

    private val width = terrain.first().length

    init {
        addPosToPath()
    }

    fun move(): Toboggon {
        x = (x + 3) % width
        y += 1
        addPosToPath()
        return this
    }

    fun run(): String {
        while (!isFinished) {
            move()
        }
        return path
    }

    private fun addPosToPath() {
        path += terrain[y][x]
    }
}
