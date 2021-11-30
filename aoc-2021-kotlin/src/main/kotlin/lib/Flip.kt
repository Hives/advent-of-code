package lib

fun <T> List<List<T>>.flip(): List<List<T>> =
    (this[0].indices).map { col ->
        (this.indices).map { row ->
            this[row][col]
        }
    }
