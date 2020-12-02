package days

import lib.Reader
import lib.oldValidatePassword
import lib.validatePassword

fun main() {
    val input = Reader("day02.txt").listOfPasswords()

    println("Number of valid passwords by old method: ${input.count { oldValidatePassword(it) }}")
    println("Number of valid passwords by new method: ${input.count { validatePassword(it) }}")
}
