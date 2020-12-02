package lib

class Reader(private val file: String) {
    fun ints() = mapLines { it.toInt() }

    fun passwordsAndNewCriteria() = getSplitPasswordDetails().map {
        PasswordAndCriteria(
            password = it[3],
            NewCriteria(
                char = it[2].single(),
                pos1 = it[0].toInt() - 1,
                pos2 = it[1].toInt() - 1
            )
        )
    }

    fun passwordsAndOldCriteria() = getSplitPasswordDetails().map {
        PasswordAndCriteria(
            password = it[3],
            OldCriteria(
                char = it[2].single(),
                min = it[0].toInt(),
                max = it[1].toInt()
            )
        )
    }

    private fun getSplitPasswordDetails() = mapLines { it.split("-", ": ", " ") }

    private fun <T> mapLines(f: (String) -> T) = read().lines().map { f(it) }

    private fun read() = javaClass.getResource("/$file").readText()
}