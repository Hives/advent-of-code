package days.day13

fun solveBusTimetable(busTimetable: List<String>): Long {
    // For notes on this solution see notes.md for this day

    val bigN = busTimetable.filter { it != "x" }
        .map { it.toInt() }
        .map { it.toLong() }
        .reduce { a, b -> a * b }

    val busMap = mutableMapOf<Int, Int>()
    busTimetable.forEachIndexed { index, s ->
        if (s != "x") busMap[s.toInt()] = index
    }

    return busMap
//        .also {
//            println("--\nbus map")
//            println(it)
//        }
        .map { (busId, offset) ->
            busId to Math.floorMod(busId - offset, busId)
        }.toMap()
//        .also {
//            println("--\nmaps")
//            println(it)
//        }
        .map { (n, a) ->
            val bigNOverLittleN = bigN / n
            Triple(a, n, euclideanAlgorithm(bigNOverLittleN, n.toLong()))
        }
        .map { (a, n, euclidSteps) ->
            Triple(a, n, extendedEuclideanAlgorithm(euclidSteps))
        }
//        .also {
//            println("--\neuclidean algo outputs")
//            it.forEach { (_, _, sol) ->
//                println(sol)
//                println((sol.n1 * sol.f1) + (sol.n2 * sol.f2))
//            }
//        }
        .map { (a, n, sol) ->
            a * sol.n1 * sol.f1
        }
//        .also {
//            println("--\nmultiply things")
//            println(it)
//        }
        .sum()
//        .also {
//            println("--\nsum them")
//            println(it)
//        }
        .let { Math.floorMod(it, bigN) }
//        .also {
//            println("--\nthe answer")
//            println(it)
//        }
}

fun euclideanAlgorithm(high: Long, low: Long): List<EuclideanAlgorithmStep> {
    val steps = mutableListOf(euclideanAlgorithmSingleStep(high, low))

    while (steps.last().remainder > 0) {
        val (_, divisor, _, remainder) = steps.last()
        steps.add(euclideanAlgorithmSingleStep(divisor, remainder))
    }
    return steps.toList()
}

fun euclideanAlgorithmSingleStep(total: Long, divisor: Long): EuclideanAlgorithmStep {
    return EuclideanAlgorithmStep(
        total = total,
        divisor = divisor,
        multiplier = total / divisor,
        remainder = total % divisor
    )
}

fun extendedEuclideanAlgorithm(steps: List<EuclideanAlgorithmStep>): ExtendedEuclideanAlgorithmStep {
    if (steps.size == 1) {
        return ExtendedEuclideanAlgorithmStep(0, 0, 0, 0)
    }

    val extendedAlgoInput = steps.toList().reversed().drop(1)

    val firstStep = extendedAlgoInput[0].let {
        ExtendedEuclideanAlgorithmStep(it.total, 1, it.divisor, -it.multiplier)
    }

    return extendedAlgoInput.drop(1).fold(firstStep) { acc, it ->
        ExtendedEuclideanAlgorithmStep(it.total, acc.f2, it.divisor, acc.f1 + (acc.f2 * -it.multiplier))
    }
}

// total = divisor * multiplier + remainder
data class EuclideanAlgorithmStep(val total: Long, val divisor: Long, val multiplier: Long, val remainder: Long)

// gcd = n1 * f1 + n2 * f2
data class ExtendedEuclideanAlgorithmStep(val n1: Long, val f1: Long, val n2: Long, val f2: Long)
