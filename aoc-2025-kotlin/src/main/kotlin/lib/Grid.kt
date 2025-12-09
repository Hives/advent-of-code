package lib

typealias Grid<T> = List<List<T>>

fun <T> Grid<T>.atOrNull(v: Vector): T? {
    require(v.x >= Int.MIN_VALUE && v.x <= Int.MAX_VALUE) { "Vector x value was outside Int bounds: ${v.x}" }
    require(v.y >= Int.MIN_VALUE && v.y <= Int.MAX_VALUE) { "Vector y value was outside Int bounds: ${v.y}" }

    return if (v.y >= 0 && v.y < this.size && v.x >= 0 && v.x < this[v.y.toInt()].size) {
        this[v.y.toInt()][v.x.toInt()]
    } else {
        null
    }
}

fun <T> Grid<T>.at(v: Vector, default: T): T =
    atOrNull(v) ?: default

fun <T> Grid<T>.flip(): Grid<T> =
    this[0].indices.map { x ->
        this.indices.map { y ->
           this[y][x]
        }
    }

/*
 Returns a sequence of Pair(Vector(x, y), cellValue)
 */
fun <T> Grid<T>.cells() =
    indices.asSequence().flatMap { y ->
        get(y).indices.asSequence().map { x ->
            Pair(Vector(x.toLong(), y.toLong()), get(y)[x])
        }
    }

fun printy(grid: Grid<Char>, things: Map<Vector, Char>) {
    grid.forEachIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val v = Vector(x.toLong(), y.toLong())
            if (v in things) things[v]
            else cell
        }
            .joinToString("")
            .also(::println)
    }
}
