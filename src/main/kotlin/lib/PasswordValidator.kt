package lib

fun validatePassword(details: PasswordDetails): Boolean {
    val (password, requiredChar, pos1, pos2) = details

    val c1 = password[pos1]
    val c2 = password[pos2]

    if ((c1 == requiredChar) xor (c2 == requiredChar)) return true
    return false
}

fun oldValidatePassword(details: OldPasswordDetails): Boolean {
    val (password, requiredChar, min, max) = details

    if (password.count { it == requiredChar } < min) return false
    if (password.count { it == requiredChar } > max) return false
    return true
}

data class PasswordDetails(val password: String, val requiredChar: Char, val pos1: Int, val pos2: Int)
data class OldPasswordDetails(val password: String, val requiredChar: Char, val min: Int, val max: Int)
