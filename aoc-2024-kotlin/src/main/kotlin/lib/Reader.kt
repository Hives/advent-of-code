package lib

class Reader(private val file: String) {
    fun string(): String = javaClass.getResource("$file")!!.readText().trimEnd()
    fun strings(): List<String> = string().lines()
    fun listOfLongs() = string().split(" ").map(String::toLong)
    fun listOfInts() = string().split(" ").map(String::toInt)
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

    fun longs(): List<Long> = strings().map { it.toLong() }
    fun ints(): List<Int> = strings().map { it.toInt() }
    fun commaSeparated() = string().split(",")
    fun vectors() = strings().map { it.split(",").let { (x, y) -> Vector(x.toInt(), y.toInt()) } }
}
