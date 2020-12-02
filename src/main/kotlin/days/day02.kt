package days

import lib.Reader

fun main() {
    val input = Reader("day02.txt")

    val oldPasswordDetails = input.listOfOldPasswordDetails()
    println("Number of valid passwords by old method: ${oldPasswordDetails.count { it.isValid }}")

    val passwordDetails = input.listOfPasswordDetails()
    println("Number of valid passwords by new method: ${passwordDetails.count { it.isValid }}")
}
