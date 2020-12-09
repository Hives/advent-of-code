package days.day09

fun List<Long>.findTwoLongsThatAddTo(total: Long): Set<Long> {
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            if (this[i] + this[j] == total) return setOf(this[i], this[j])
        }
    }
    return emptySet()
}

fun List<Long>.findContiguousLongsThatAddTo(total: Long): Pair<Int, Int>? {
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            if (this.subList(i, j + 1).sum() == total) return Pair(i, j)
        }
    }
    return null
}
