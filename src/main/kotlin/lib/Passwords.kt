package lib

fun String.extractPassword(): String = this.split(": ").last()

fun String.extractOldCriteria(): Criteria = this
    .extractCriteriaParts()
    .let { OldCriteria(it[2].single(), it[0].toInt(), it[1].toInt()) }

fun String.extractNewCriteria(): Criteria = this
    .extractCriteriaParts()
    .let { NewCriteria(it[2].single(), it[0].toInt() - 1, it[1].toInt() - 1) }

private fun String.extractCriteriaParts() =
    this.split(": ").first().split("-", " ")

sealed class Criteria {
    abstract fun validate(password: String): Boolean
}

data class OldCriteria(
    val char: Char,
    val min: Int,
    val max: Int
) : Criteria() {
    override fun validate(password: String) = password.count { it == char } in min..max
}

data class NewCriteria(
    val char: Char,
    val pos1: Int,
    val pos2: Int
): Criteria() {
    override fun validate(password: String) = (password[pos1] == char) xor (password[pos2] == char)
}
