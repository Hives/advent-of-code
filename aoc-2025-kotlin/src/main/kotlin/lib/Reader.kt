package lib

class Reader(private val resourcePath: String) {
    val file: String

    init {
        val resource = javaClass.getResource(resourcePath)
        require(resource != null) { "Could not find resource: $resourcePath" }
        file = resource.readText().lines().dropLastWhile { it.isEmpty() }.joinToString("\n")
    }

    fun string(): String = file
    fun strings(): List<String> = file.lines()
    fun listOfLongs() = file.split(" ").map(String::toLong)
    fun listOfInts() = file.split(" ").map(String::toInt)
    fun listOfListOfLongs() = file.lines().map { it.split(" ").map(String::toLong) }
    fun listOfListOfInts() = file.lines().map { it.split(" ").map(String::toInt) }
    fun chars(): List<Char> = file.toList()
    fun grid(): Grid<Char> = file.lines().map(String::toList)
    fun intGrid(): Grid<Int> = file.lines().map {
        it.split("").mapNotNull { c ->
            if (c == "") null else c.toInt()
        }
    }

    fun <T, R> mapAnswers(
        m1: (String) -> T,
        m2: (String) -> R
    ): Pair<T, R> =
        file.lines().let { Pair(m1(it[0]), m2(it[1])) }

    fun longs(): List<Long> = file.lines().map { it.toLong() }
    fun ints(): List<Int> = file.lines().map { it.toInt() }
    fun commaSeparated() = file.split(",")
    fun vectors() = file.lines().map { it.split(",").let { (x, y) -> Vector(x.toLong(), y.toLong()) } }
}
