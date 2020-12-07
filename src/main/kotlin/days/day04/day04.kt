package days.day04

import lib.Reader

fun main() {
    val passportData = Reader("day04.txt").string().mapToPassportData()
    val passports = passportData.map { Passport.from(it) }
    println("part 1: ${passports.count { it.isValid }}")
    println("part 2: ${passports.count { it.isValid2 }}")
}

