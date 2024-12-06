package lib

import kotlin.math.abs
import kotlin.math.sign

data class Vector(val x: Int, val y: Int) : Comparable<Vector> {
    val manhattanDistance: Int
        get() = abs(x) + abs(y)

    operator fun times(n: Int) = Vector(
        x = x * n,
        y = y * n
    )

    operator fun plus(other: Vector) = Vector(
        x = this.x + other.x,
        y = this.y + other.y
    )

    operator fun plus(other: Direction) = this + other.vector

    operator fun minus(other: Vector) = Vector(
        x = this.x - other.x,
        y = this.y - other.y
    )

    fun isAdjacentTo(other: Vector): Boolean {
        val diff = this - other
        return abs(diff.x) <= 1 && abs(diff.y) <= 1
    }

    fun rotateRight() = Vector(-y, x)
    fun rotateLeft() = Vector(y, -x)

    fun rotateQuarterTurnsCCW(quarterTurns: Int = 1) =
        this.repeatedlyApply(quarterTurns % 4) {
            Vector(
                x = -it.y,
                y = it.x
            )
        }

    fun pathTo(other: Vector): List<Vector> {
        val direction = (other - this).let { Vector(it.x.sign, it.y.sign) }
        require(direction in CompassDirection.values().map { it.vector }) { "Vectors must be in a horizontal or vertical line" }

        val path = mutableListOf(this)
        while (path.last() != other) path.add(path.last() + direction)
        return path.toList()
    }

    override fun compareTo(other: Vector): Int {
        if (this.y > other.y) return 1
        if (this.y < other.y) return -1
        if (this.x > other.x) return 1
        if (this.x < other.x) return -1
        return 0
    }

    val neighbours: Set<Vector>
        get() = CompassDirection.values().map { unit -> unit.vector + this }.toSet()

    val surrounding: Set<Vector>
        get() = AllCompassDirection.values().map { unit -> unit.vector + this }.toSet()

    override fun toString() = "v{$x, $y}"
}

interface Direction {
   val vector: Vector
}

enum class CompassDirection(override val vector: Vector): Direction {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1, 0));

    fun opposite(): Direction =
        when (this) {
            N -> S
            S -> N
            E -> W
            W -> E
        }

    companion object {
        fun from(input: String) = when (input) {
            "N", "U" -> N
            "S", "D" -> S
            "E", "R" -> E
            "W", "L" -> W
            else -> throw Exception("Unknown direction: $input")
        }
    }
}

enum class AllCompassDirection(override val vector: Vector): Direction {
    N(Vector(0, 1)),
    NE(Vector(1, 1)),
    E(Vector(1, 0)),
    SE(Vector(1, -1)),
    S(Vector(0, -1)),
    SW(Vector(-1, -1)),
    W(Vector(-1, 0)),
    NW(Vector(-1, 1))
}
