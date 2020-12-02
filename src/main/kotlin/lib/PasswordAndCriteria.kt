package lib

data class PasswordAndCriteria(private val password: String, private val criteria: Criteria) {
    fun validate() = criteria.validate(password)
}

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
