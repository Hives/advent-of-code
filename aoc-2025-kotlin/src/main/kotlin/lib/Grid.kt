package lib

typealias Grid<T> = List<List<T>>

fun <T> Grid<T>.atOrNull(v: Vector): T? =
    if (v.y >= 0 && v.y < this.size && v.x >= 0 && v.x < this[v.y].size) {
        this[v.y][v.x]
    } else {
        null
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
            Pair(Vector(x, y), get(y)[x])
        }
    }

fun printy(grid: Grid<Char>, things: Map<Vector, Char>) {
    grid.forEachIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val v = Vector(x, y)
            if (v in things) things[v]
            else cell
        }
            .joinToString("")
            .also(::println)
    }
}
