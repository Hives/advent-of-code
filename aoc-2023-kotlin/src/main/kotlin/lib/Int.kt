package lib

import kotlin.Int

fun Int.pow(exponent: Int): Int {
    require(exponent >= 0) { "exponent must be greater than 0, but was $exponent"}

    tailrec fun go(i: Int, acc: Int): Int =
        if (i == 0) acc
        else go(i - 1, this * acc)
    
    return go(exponent, 1)
}
