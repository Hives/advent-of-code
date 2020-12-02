package days

import lib.Reader
import lib.extractNewCriteria
import lib.extractOldCriteria
import lib.extractPassword

fun main() {
    val input = Reader("day02.txt").strings()

    input.map { Pair(it.extractOldCriteria(), it.extractPassword()) }
        .count { (criteria, password) -> criteria.validate(password) }
        .also { println("Number of valid passwords by old method: $it") }

    input.map { Pair(it.extractNewCriteria(), it.extractPassword()) }
        .count { (criteria, password) -> criteria.validate(password) }
        .also { println("Number of valid passwords by new method: $it") }
}
