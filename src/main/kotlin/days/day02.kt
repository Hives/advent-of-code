package days

import lib.Reader

fun main() {
    val input1 = Reader("day02.txt").listOfOldPasswordDetails()
    println("Number of valid passwords by old method: ${input1.count { it.isValid }}")

    val input2 = Reader("day02.txt").listOfPasswordDetails()
    println("Number of valid passwords by new method: ${input2.count { it.isValid }}")
}
