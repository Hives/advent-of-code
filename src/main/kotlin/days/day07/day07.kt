package days.day07

import lib.Reader

fun main() {
    val input = Reader("day07.txt").string()
    val bagRules = input.parseBagRules()
    val myBag = "shiny gold"

    val parents = myBag.getAllContainers(bagRules)
    println(parents.size)

    println(myBag.countContents(bagRules))
}

