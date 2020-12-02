package days

import lib.Reader
import lib.validateOldPassword
import lib.validatePassword

fun main() {
    val input1 = Reader("day02.txt").listOfOldPasswordDetails()
    println("Number of valid passwords by old method: ${input1.count { validateOldPassword(it) }}")

    val input2 = Reader("day02.txt").listOfPasswordDetails()
    println("Number of valid passwords by new method: ${input2.count { validatePassword(it) }}")
}
