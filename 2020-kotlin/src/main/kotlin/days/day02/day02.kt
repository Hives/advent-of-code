package days.day02

import lib.Reader

fun main() {
    val input = Reader("day02.txt").strings()

    input.map { Pair(it.extractOldPolicy(), it.extractPassword()) }
        .count { (criteria, password) -> criteria.validate(password) }
        .also { println("Number of valid passwords by old method: $it") }

    input.map { Pair(it.extractNewPolicy(), it.extractPassword()) }
        .count { (criteria, password) -> criteria.validate(password) }
        .also { println("Number of valid passwords by new method: $it") }

    // 2nd solution using regex

    input.map { extractPasswordAndOldPolicy(it) }
        .count { (password, policy) -> policy.validate(password) }
        .also { println("Number of valid passwords by old method using regex: $it") }

    input.map { extractPasswordAndNewPolicy(it) }
        .count { (password, policy) -> policy.validate(password) }
        .also { println("Number of valid passwords by old method using regex: $it") }
}
