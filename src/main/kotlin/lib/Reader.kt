package lib

class Reader(private val file: String) {
    fun listOfInts() = read().lines().map { it.toInt() }

    fun listOfOldPasswordDetails() = splitPasswordDetails().map {
        OldPasswordDetails(
            password = it[3],
            requiredChar = it[2].single(),
            min = it[0].toInt(),
            max = it[1].toInt()
        )
    }

    fun listOfPasswordDetails() = splitPasswordDetails().map {
        PasswordDetails(
            password = it[3],
            requiredChar = it[2].single(),
            pos1 = it[0].toInt() - 1,
            pos2 = it[1].toInt() - 1
        )
    }

    private fun splitPasswordDetails() = read().lines().map { it.split("-", ": ", " ") }

    private fun read() = javaClass.getResource("/$file").readText()
}