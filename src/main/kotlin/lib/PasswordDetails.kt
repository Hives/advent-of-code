package lib


data class PasswordDetails(val password: String, val requiredChar: Char, val pos1: Int, val pos2: Int) {
    val isValid
        get() = (password[pos1] == requiredChar) xor (password[pos2] == requiredChar)
}

data class OldPasswordDetails(val password: String, val requiredChar: Char, val min: Int, val max: Int) {
    val isValid
        get() = when {
            password.count { it == requiredChar } < min -> false
            password.count { it == requiredChar } > max -> false
            else -> true
        }
}
