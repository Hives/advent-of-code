package lib

class Reader(private val file: String) {
    fun string(): String = javaClass.getResource("$file")!!.readText().trimEnd()
    fun strings(): List<String> = string().lines()
    fun listOfListOfLongs(): List<List<Long>> = strings().map { it.split(" ").map(String::toLong) }
    fun chars(): List<Char> = string().toList()
    fun grid(): Grid = strings().map(String::toList)
    fun numbers(): List<Long> = strings().map { it.toLong() }
    fun commaSeparated() = string().split(",")
}

typealias Grid = List<List<Char>>
