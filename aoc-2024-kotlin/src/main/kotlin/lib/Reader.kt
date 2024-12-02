package lib

class Reader(private val file: String) {
    fun string(): String = javaClass.getResource("$file")!!.readText().trimEnd()
    fun strings(): List<String> = string().lines()
    fun listOfListOfLongs() = strings().map { it.split(" ").map(String::toLong) }
    fun listOfListOfInts() = strings().map { it.split(" ").map(String::toInt) }
    fun chars(): List<Char> = string().toList()
    fun grid(): Grid = strings().map(String::toList)
    fun intGrid(): IntGrid = strings().map {
        it.split("").mapNotNull { c ->
            if (c == "") null else c.toInt()
        }
    }

    fun numbers(): List<Long> = strings().map { it.toLong() }
    fun commaSeparated() = string().split(",")
}

typealias Grid = List<List<Char>>
typealias IntGrid = List<List<Int>>
