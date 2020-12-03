package lib

fun String.extractPassword(): String = this.split(": ").last()

fun String.extractOldCriteria() =
    this.extractCriteriaParts()
        .let {
            OldCriteria(
                char = it[2].single(),
                min = it[0].toInt(),
                max = it[1].toInt()
            )
        }

fun String.extractNewCriteria() =
    this.extractCriteriaParts()
        .let {
            NewCriteria(
                char = it[2].single(),
                pos1 = it[0].toInt() - 1,
                pos2 = it[1].toInt() - 1
            )
        }

private fun String.extractCriteriaParts() =
    this.split(": ").first().split("-", " ")

data class OldCriteria(
    val char: Char,
    val min: Int,
    val max: Int
) {
    fun validate(password: String) = password.count { it == char } in min..max
}

data class NewCriteria(
    val char: Char,
    val pos1: Int,
    val pos2: Int
) {
    fun validate(password: String) = (password[pos1] == char) xor (password[pos2] == char)
}
