package lib

import kotlin.math.abs

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

    operator fun minus(other: Vector) = Vector(
        x = this.x - other.x,
        y = this.y - other.y
    )

    fun rotateQuarterTurnsCCW(quarterTurns: Int = 1) =
        this.repeatedlyApply(quarterTurns % 4) {
            Vector(
                x = -it.y,
                y = it.x
            )
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

enum class Direction(val vector: Vector) {
    U(Vector(0, 1)),
    D(Vector(0, -1)),
    R(Vector(1, 0)),
    L(Vector(-1, 0))
}

enum class CompassDirection(val vector: Vector) {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1, 0))
}

enum class AllCompassDirection(val vector: Vector) {
    N(Vector(0, 1)),
    NE(Vector(1, 1)),
    E(Vector(1, 0)),
    SE(Vector(1, -1)),
    S(Vector(0, -1)),
    SW(Vector(-1, -1)),
    W(Vector(-1, 0)),
    NW(Vector(-1, 1))
}
