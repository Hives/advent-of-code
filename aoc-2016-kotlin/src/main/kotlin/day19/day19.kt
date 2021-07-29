package day19

const val puzzleInput = 3017957

fun main() {
    (1..500).forEach { n ->
        println("with n = $n: ${solveForNBruteForce(n)} vs ${solveForNIteratively(n)}")
    }
//    println(solveForNIteratively(puzzleInput))
}

fun solveForNIteratively(n: Int): Int {
    tailrec fun go(currentN: Int, prev: Int): Int {
        val next = when {
            currentN - 1 == prev -> 1
            prev >= currentN / 2 -> prev + 2
            else -> prev + 1
        }

        return if (currentN == n) next
        else go(currentN + 1, next)
    }

    return go(2, 1)
}

fun solveForNBruteForce(n: Int): Int {
    val elves = MutableList(n) { it + 1 }
    var current = 0

    while (elves.size > 1) {
        if (elves.size % 1000 == 0) {
            println("${elves.size} elves left")
        }

        val target = (current + elves.size / 2) % elves.size
        elves.removeAt(target)
        if (target > current) current++
        current %= elves.size
    }

    return elves.single()
}
