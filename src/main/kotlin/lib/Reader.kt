package lib

class Reader(private val file: String) {
    fun listOfInts() = read().lines().map { it.toInt() }

    private fun read() = javaClass.getResource("/$file").readText()
}