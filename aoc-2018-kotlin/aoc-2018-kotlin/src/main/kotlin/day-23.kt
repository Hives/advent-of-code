import lib.Reader
import lib.Vector3

fun main() {
    val input = Reader("day-23.txt").strings()

    val regex = """pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(\d+)""".toRegex()

    val nanobots = input.map {
        val (x, y, z, r) = regex.matchEntire(it)!!.destructured
        Nanobot(Vector3(x.toInt(), y.toInt(), z.toInt()), r.toInt())
    }

    nanobots.maxBy { it.range }.also { strongest ->
        nanobots.count {
            (it.location - strongest.location).manhattanDistance <= strongest.range
        }.also { println(it) }
    }
}

private data class Nanobot(val location: Vector3, val range: Int)