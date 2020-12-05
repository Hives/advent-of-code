package days.day02

import lib.Reader

fun main() {
    val input = Reader("day02.txt").strings()

    input.map { Pair(it.extractOldCriteria(), it.extractPassword()) }
        .count { (criteria, password) -> criteria.validate(password) }
        .also { println("Number of valid passwords by old method: $it") }

    input.map { Pair(it.extractNewCriteria(), it.extractPassword()) }
        .count { (criteria, password) -> criteria.validate(password) }
        .also { println("Number of valid passwords by new method: $it") }
}
