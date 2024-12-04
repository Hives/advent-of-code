package lib

typealias Grid<T> = List<List<T>>

fun <T> Grid<T>.at(v: Vector, default: T): T =
    if (v.y >= 0 && v.y < this.size && v.x >= 0 && v.x < this[v.y].size) {
        this[v.y][v.x]
    } else {
        default
    }
