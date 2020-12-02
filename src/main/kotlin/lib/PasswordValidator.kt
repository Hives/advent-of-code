package lib

fun validatePassword(details: PasswordDetails): Boolean {
    val (password, requiredChar, min, max) = details
    if (password.count { it == requiredChar } < min) return false
    if (password.count { it == requiredChar } > max) return false
    return true
}

data class PasswordDetails(val password: String, val requiredChar: Char, val min: Int, val max: Int)
