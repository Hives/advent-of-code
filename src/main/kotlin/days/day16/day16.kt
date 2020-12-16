package days.day16

import lib.Reader
import lib.time

fun main() {
//    val (first, second, third) = splitInput(exampleInput1)
    val (first, second, third) = splitInput(exampleInput2)
//    val (first, second, third) = splitInput(puzzleInput)

    val rulesRegex = """^([a-z\s]+): (\d+)-(\d+) or (\d+)-(\d+)$""".toRegex()
    val rules = first.split("\n")
        .map { rulesRegex.find(it)!!.destructured }
        .map { (name, a1, a2, b1, b2) -> name to Pair(a1.toInt()..a2.toInt(), b1.toInt()..b2.toInt()) }
        .toMap()
    val myTicket = parseTicket(second.split("\n")[1])
    val nearbyTickets = third.split("\n").drop(1).map { parseTicket(it) }

//    time("part 1") { part1(rules, nearbyTickets) }

    val potentiallyValidTickets = nearbyTickets.filterNot { ticket ->
        ticket.someFieldsAreInvalid(rules)
    }

//    potentiallyValidTickets.flip()
//        .also { println(it.size) }
}

typealias Rule = Pair<ClosedRange<Int>, ClosedRange<Int>>
typealias Rules = Map<String, Rule>

fun part1(rules: Rules, tickets: List<List<Int>>): Int {
    var ticketScanningErrorRate = 0

    tickets.forEach { ticket ->
        for (field in ticket) {
            if (rules.all { field.isNotValidByRule(it.value) }) ticketScanningErrorRate += field
        }
    }

    return ticketScanningErrorRate
}

fun List<List<Int>>.flip(): List<List<Int>> =
    (this[0].indices).map { col ->
        (this.indices).map { row ->
            this[row][col]
        }
    }

fun List<Int>.someFieldsAreInvalid(rules: Rules) =
    this.any { field -> rules.all { rule -> field.isNotValidByRule(rule.value) } }

fun parseTicket(input: String) = input.split(",").map { it.toInt() }

fun Int.isNotValidByRule(rule: Rule) = !this.isValidByRule(rule)
fun Int.isValidByRule(rule: Rule) = this in rule.first || this in rule.second

fun splitInput(input: String) = input.trim().split("\n\n")

private const val exampleInput1 = "class: 1-3 or 5-7\n" +
        "row: 6-11 or 33-44\n" +
        "seat: 13-40 or 45-50\n" +
        "\n" +
        "your ticket:\n" +
        "7,1,14\n" +
        "\n" +
        "nearby tickets:\n" +
        "7,3,47\n" +
        "40,4,50\n" +
        "55,2,20\n" +
        "38,6,12\n"

private const val exampleInput2 = "class: 0-1 or 4-19\n" +
        "row: 0-5 or 8-19\n" +
        "seat: 0-13 or 16-19\n" +
        "\n" +
        "your ticket:\n" +
        "11,12,13\n" +
        "\n" +
        "nearby tickets:\n" +
        "3,9,18\n" +
        "15,1,5\n" +
        "5,14,9\n"

private val puzzleInput = Reader("day16.txt").string()
