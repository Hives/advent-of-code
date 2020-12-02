package lib


data class PasswordDetails(val password: String, val requiredChar: Char, val pos1: Int, val pos2: Int) {
    fun validate() = (password[pos1] == requiredChar) xor (password[pos2] == requiredChar)
}

data class OldPasswordDetails(val password: String, val requiredChar: Char, val min: Int, val max: Int) {
    fun validate() = password.count { it == requiredChar } in min..max
}
