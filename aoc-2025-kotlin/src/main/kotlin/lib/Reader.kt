package lib

class Reader(private val file: String) {
    fun strings(): List<String> = javaClass.getResource("$file")!!.readText().lines().dropLastWhile { it.isEmpty() }
    fun string(): String = strings().joinToString("\n")
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
    fun <T, R> mapAnswers(
        m1: (String) -> T,
        m2: (String) -> R
    ): Pair<T, R> =
        strings().let { Pair(m1(it[0]), m2(it[1])) }

    fun digits(): List<Int> = strings().first().split("").filter { it.isNotEmpty() }.map(String::toInt)

    fun longs(): List<Long> = strings().map { it.toLong() }
    fun ints(): List<Int> = strings().map { it.toInt() }
    fun commaSeparated() = string().split(",")
    fun vectors() = strings().map { it.split(",").let { (x, y) -> Vector(x.toLong(), y.toLong()) } }
}
