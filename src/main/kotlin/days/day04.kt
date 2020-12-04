package days

import lib.Passport
import lib.Reader
import lib.mapToPassportData

fun main() {
    val passportData = Reader("day04.txt").strings().mapToPassportData()
    val passports = passportData.map { Passport.from(it) }
    println(passports.count { it.isValid })
    println(passports.count { it.isValid2 })
}

