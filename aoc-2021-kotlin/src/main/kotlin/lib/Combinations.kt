package lib

// dangerous recursion 😲
fun <T> List<T>.combinations(n: Int): List<List<T>> =
    when (n) {
        0 -> emptyList()
        1 -> this.map { listOf(it) }
        else -> {
            this.indices.flatMap { i ->
                val x = this[i]
                val xs = this.subList(i + 1, this.size)
                xs.combinations(n - 1).map { listOf(x) + it }
            }
        }
    }
