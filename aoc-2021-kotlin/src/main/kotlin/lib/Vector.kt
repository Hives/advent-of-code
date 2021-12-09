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
        get() = UnitVector.values().map { unit -> unit.vector + this }.toSet()

    override fun toString() = "V[$x, $y]"
}

enum class UnitVector(val vector: Vector) {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1, 0))
}

private tailrec fun <T> T.repeatedlyApply(n: Int, f: (T) -> T): T =
    if (n == 0) this
    else f(this).repeatedlyApply(n - 1, f)
