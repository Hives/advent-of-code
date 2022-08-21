package lib

class Reader(private val file: String) {
    fun string(): String = javaClass.getResource("/$file")!!.readText().trim()
    fun strings(): List<String> = string().lines()
    fun chars(): List<Char> = string().toList()
    fun ints(): List<Int> = strings().map { it.toInt() }
    fun longs(): List<Long> = strings().map { it.toLong() }
}