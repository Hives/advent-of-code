package days.day02

private val passwordPolicyRegex = """(\d+)-(\d+) (\w): (\w+)""".toRegex()

fun extractPasswordAndOldPolicy(input: String): Pair<String, OldPolicy> {
    val (min, max, char, password) = passwordPolicyRegex.find(input)!!.destructured
    return Pair(password, OldPolicy(char.single(), min.toInt(), max.toInt()))
}

fun extractPasswordAndNewPolicy(input: String): Pair<String, NewPolicy> {
    val (pos1, pos2, char, password) = passwordPolicyRegex.find(input)!!.destructured
    return Pair(password, NewPolicy(char.single(), pos1.toInt() - 1, pos2.toInt() - 1))
}

fun String.extractPassword(): String = this.split(": ").last()

fun String.extractOldPolicy() =
    this.extractPolicyParts()
        .let {
            OldPolicy(
                char = it[2].single(),
                min = it[0].toInt(),
                max = it[1].toInt()
            )
        }

fun String.extractNewPolicy() =
    this.extractPolicyParts()
        .let {
            NewPolicy(
                char = it[2].single(),
                pos1 = it[0].toInt() - 1,
                pos2 = it[1].toInt() - 1
            )
        }

private fun String.extractPolicyParts() =
    this.split(": ").first().split("-", " ")

data class OldPolicy(
    val char: Char,
    val min: Int,
    val max: Int
) {
    fun validate(password: String) = password.count { it == char } in min..max
}

data class NewPolicy(
    val char: Char,
    val pos1: Int,
    val pos2: Int
) {
    fun validate(password: String) = (password[pos1] == char) xor (password[pos2] == char)
}
