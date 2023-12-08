package lib

fun gcd(n1: Long, n2: Long): Long {
    require(n1 > 0 && n2 > 0)
    // Euclid's algorithm
    val (bigger, smaller) = if (n1 >= n2) Pair(n1, n2) else Pair(n2, n1)
    val remainder = bigger % smaller
    return if (remainder == 0L) smaller
    else gcd(smaller, remainder)
}

fun lcm(n1: Long, n2: Long): Long {
    require(n1 > 0 && n2 > 0)
    return (n1 * n2) / gcd(n1, n2)
}

fun lcm(ns: List<Long>): Long = ns.reduce(::lcm)
