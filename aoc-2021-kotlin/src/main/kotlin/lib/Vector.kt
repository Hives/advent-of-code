package lib

import kotlin.math.abs

data class Vector(val x: Int, val y: Int) {
    val manhattanDistance: Int
        get() = abs(x) + abs(y)

    operator fun times(n: Int) = Vector(
        x = x * n,
        y = y * n
    )

    operator fun plus(vector: Vector) = Vector(
        x = this.x + vector.x,
        y = this.y + vector.y
    )

    fun rotateQuarterTurnsCCW(quarterTurns: Int = 1) =
        this.repeatedlyApply(quarterTurns % 4) {
            Vector(
                x = -it.y,
                y = it.x
            )
        }
}

enum class UnitVector(val vector: Vector) {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1, 0))
}

tailrec fun <T> T.repeatedlyApply(n: Int, f: (T) -> T): T =
    if (n == 0) this
    else f(this).repeatedlyApply(n - 1, f)
