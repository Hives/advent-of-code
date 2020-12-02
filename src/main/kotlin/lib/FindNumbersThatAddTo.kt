package lib

fun List<Int>.findTwoNumbersThatAddTo(total: Int): Set<Int> {
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            if (this[i] + this[j] == total) return setOf(this[i], this[j])
        }
    }
    return emptySet()
}

fun List<Int>.findThreeNumbersThatAddTo(total: Int): Set<Int> {
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            for (k in j + 1 until this.size) {
                if (this[i] + this[j] + this[k] == total) return setOf(this[i], this[j], this[k])
            }
        }
    }
    return emptySet()
}
