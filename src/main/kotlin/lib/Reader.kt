package lib

class Reader(private val file: String) {
    fun string() = javaClass.getResource("/$file").readText().trim()
    fun strings() = string().lines()
    fun ints() = strings().map { it.toInt() }
}