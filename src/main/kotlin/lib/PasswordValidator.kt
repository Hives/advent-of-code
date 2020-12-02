package lib

fun validatePassword(details: PasswordDetails): Boolean =
    with(details) {
        if ((password[pos1] == requiredChar) xor (password[pos2] == requiredChar)) return true
        return false
    }

fun validateOldPassword(details: OldPasswordDetails): Boolean =
    with(details) {
        if (password.count { it == requiredChar } < min) return false
        if (password.count { it == requiredChar } > max) return false
        return true
    }

data class PasswordDetails(val password: String, val requiredChar: Char, val pos1: Int, val pos2: Int)
data class OldPasswordDetails(val password: String, val requiredChar: Char, val min: Int, val max: Int)
