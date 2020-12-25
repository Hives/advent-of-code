package days.day25

const val bigPrime = 20201227L

fun transform(privateKey: Long, subjectNumber: Long): Long =
    modularExponentiation2(subjectNumber, privateKey, bigPrime)

fun findPrivateKey(publicKey: Long): Long =
    (1..bigPrime).first { privateKey ->
        transform(privateKey, 7) == publicKey
    }.toLong()

fun modularExponentiation2(base: Long, exponent: Long, modulo: Long): Long {
    val powersOf2 = decomposeToPowersOf2(exponent)
    val powersOf2Map = calculateNToPowersOf2(base, modulo, powersOf2.maxOrNull()!!)
    return powersOf2.map { powersOf2Map[it]!!.toLong() }.reduce { a, b -> a * b % modulo }
}

fun calculateNToPowersOf2(
    n: Long,
    modulo: Long,
    maxPowerOf2: Int
): Map<Int, Long> {
    val map = mutableMapOf(0 to n)
    (1..maxPowerOf2).forEach { powerOf2 ->
        map[powerOf2] = map[powerOf2 - 1]!!.let { it * it } % modulo
    }
    return map.toMap()
}

fun decomposeToPowersOf2(n: Long): MutableSet<Int> {
    val powersOf2 = mutableSetOf<Int>()
    tailrec fun String.go(n: Int = 0) {
        if (this.isEmpty()) return
        if (this.first() == '1') powersOf2.add(n)
        this.drop(1).go(n + 1)
    }
    n.toInt().toBinaryString().reversed().go()
    return powersOf2
}

fun modularExponentiation(base: Long, exponent: Long, modulo: Long): Long {
    tailrec fun go(value: Long, iterations: Long): Long =
        if (iterations == 0L) value
        else go((value * base) % modulo, iterations - 1)

    return go(1L, exponent)
}

fun Int.toBinaryString(): String = Integer.toBinaryString(this)
