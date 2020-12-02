package lib

class Reader(private val file: String) {
    fun strings() = javaClass.getResource("/$file").readText().lines()
    fun ints() = strings().map { it.toInt() }
}