package lib

fun List<Int>.combinations(n: Int): List<List<Int>> =
        if (n == 1) this.map { listOf(it) }
        else {
            this.flatMapIndexed { index, i ->
                val remainder = this.subList(index + 1, this.size)
                // TODO: dangerous recursion!
                remainder.combinations(n - 1).map { (it + i).sorted() }
            }
        }