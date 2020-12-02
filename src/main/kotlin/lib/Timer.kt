package lib

import kotlin.system.measureTimeMillis

fun time(f: () -> Unit) {
    measureTimeMillis(f).also { println("completed in $it milliseconds") }
}

