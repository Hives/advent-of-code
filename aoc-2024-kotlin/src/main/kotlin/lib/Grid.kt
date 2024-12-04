package lib

typealias Grid<T> = List<List<T>>

fun <T> Grid<T>.at(v: Vector, default: T): T =
    if (v.y >= 0 && v.y < this.size && v.x >= 0 && v.x < this[v.y].size) {
        this[v.y][v.x]
    } else {
        default
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
