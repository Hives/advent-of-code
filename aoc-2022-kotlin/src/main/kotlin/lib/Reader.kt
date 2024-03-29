package lib

class Reader(private val file: String) {
    fun string(): String = javaClass.getResource("/$file")!!.readText().trimEnd()
    fun strings(): List<String> = string().lines()
    fun chars(): List<Char> = string().toList()
    fun grid() = strings().map(String::toList)
    fun numbers(): List<Long> = strings().map { it.toLong() }
}
