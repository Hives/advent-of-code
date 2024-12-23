package days.day23

import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day23/input.txt").strings()
    val exampleInput = Reader("/day23/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1200)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer("ag,gh,hh,iv,jx,nq,oc,qm,rb,sm,vm,wu,zr")
}

fun part1(input: List<String>): Int {
    val connectionMap = parseInput(input)
    val computers = connectionMap.keys
    return computers.asSequence()
        .filter { it.startsWith("t") }
        .flatMap { connectionMap.findNetworksOfThree(it) }
        .distinct()
        .count()
}

fun part2(input: List<String>): String {
    val connectionMap = parseInput(input)
    val networks = getNetworks(connectionMap)
    val largest = networks.maxBy { it.size }
    return largest.sorted().joinToString(separator = ",")
}

fun getNetworks(connectionMap: Map<String, List<String>>): List<List<String>> {
    val allComputers = connectionMap.keys.toList()

    fun go(
        networks: List<List<String>>,
        newComputer: String,
        remainingComputers: List<String>,
    ): Pair<List<List<String>>, List<String>> {
        if (remainingComputers.isEmpty()) {
           return Pair(networks, emptyList())
        }

        val newNetworks = networks.map { network ->
            if (network.all { it in connectionMap[newComputer]!! }) {
                (network + newComputer)
            } else {
                network
            }
        }

        val nextNewComputer = remainingComputers.last()
        val nextRemainingComputers = remainingComputers.subList(0, remainingComputers.size - 1)

        return go(newNetworks + listOf(emptyList()), nextNewComputer, nextRemainingComputers)
    }

    return go(
        networks = listOf(emptyList()),
        newComputer = allComputers.last(),
        remainingComputers = allComputers.subList(0, connectionMap.keys.size - 1),
    ).first
}

fun Map<String, List<String>>.findNetworksOfThree(computer: String): List<List<String>> {
    val connected = this[computer]!!
    val twos = connected.map { listOf(it, computer) }
    return twos.flatMap { (c1, c2) ->
        val extras = this[c1]!!.intersect(this[c2]!!)
        extras.map { c3 ->
            listOf(c1, c2, c3).sorted()
        }
    }.distinct()
}

fun parseInput(input: List<String>): Map<String, List<String>> {
    val pairs = input.map { it.split("-") }
    val computers = pairs.flatten().distinct()
    return computers.associateWith { c1 ->
        pairs.filter { it.contains(c1) }.map { it.single { it != c1 } }
    }
}
