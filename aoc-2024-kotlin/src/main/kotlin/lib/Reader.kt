package lib

class Reader(private val file: String) {
    fun string(): String = javaClass.getResource("$file")!!.readText().trimEnd()
    fun strings(): List<String> = string().lines()
    fun listOfLongs() = string().split(" ").map(String::toLong)
    fun listOfListOfLongs() = strings().map { it.split(" ").map(String::toLong) }
    fun listOfListOfInts() = strings().map { it.split(" ").map(String::toInt) }
    fun chars(): List<Char> = string().toList()
    fun grid(): Grid<Char> = strings().map(String::toList)
    fun intGrid(): Grid<Int> = strings().map {
        it.split("").mapNotNull { c ->
            if (c == "") null else c.toInt()
        }
    }

    fun digits(): List<Int> = strings().first().split("").filter { it.isNotEmpty() }.map(String::toInt)

    fun numbers(): List<Long> = strings().map { it.toLong() }
    fun commaSeparated() = string().split(",")
}
