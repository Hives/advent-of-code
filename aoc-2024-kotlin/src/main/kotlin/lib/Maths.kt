package lib

tailrec fun gcd(n1: Long, n2: Long): Long {
    require(n1 >= 0 && n2 >= 0)
    // Euclid's algorithm
    return if (n2 == 0L) n1
    else gcd(n2, n1 % n2)
}

fun lcm(n1: Long, n2: Long): Long {
    require(n1 > 0 && n2 > 0)
    return (n1 * n2) / gcd(n1, n2)
}

fun lcm(ns: List<Long>): Long = ns.reduce(::lcm)
