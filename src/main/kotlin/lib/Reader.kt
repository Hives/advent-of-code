package lib

class Reader(private val file: String) {
    fun listOfInts() = mapLines { it.toInt() }

    fun listOfPasswordDetails() = getSplitPasswordDetails().map {
        PasswordDetails(
            password = it[3],
            requiredChar = it[2].single(),
            pos1 = it[0].toInt() - 1,
            pos2 = it[1].toInt() - 1
        )
    }

    fun listOfOldPasswordDetails() = getSplitPasswordDetails().map {
        OldPasswordDetails(
            password = it[3],
            requiredChar = it[2].single(),
            min = it[0].toInt(),
            max = it[1].toInt()
        )
    }

    private fun getSplitPasswordDetails() = mapLines { it.split("-", ": ", " ") }

    private fun <T> mapLines(f: (String) -> T) = read().lines().map { f(it) }

    private fun read() = javaClass.getResource("/$file").readText()
}